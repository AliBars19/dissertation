#THIS DECIDES THE ORDER OF TRAINS AND HOW WERE GOING TO HAVE ONE UNIFIED DIRECTION

#^^ i just rememberd we deadass have a direction column smh

#all these station codes were pulled from the 48 hour log data

VICTORIA_NORTH_TO_SOUTH = [
    "940GZZLUWWL",   # Walthamstow Central
    "940GZZLUBLR",   # Blackhorse Road
    "940GZZLUTMH",   # Tottenham Hale
    "940GZZLUSVS",   # Seven Sisters
    "940GZZLUFPK",   # Finsbury Park
    "940GZZLUHAI",   # Highbury & Islington
    "940GZZLUKSX",   # King's Cross St. Pancras
    "940GZZLUEUS",   # Euston
    "940GZZLUWRR",   # Warren Street
    "940GZZLUOXC",   # Oxford Circus
    "940GZZLUGPK",   # Green Park
    "940GZZLUVIC",   # Victoria
    "940GZZLUPCO",   # Pimlico
    "940GZZLUVXL",   # Vauxhall
    "940GZZLUSKW",   # Stockwell
    "940GZZLUBXN",   # Brixton
]

VICTORIA_SOUTH_TO_NORTH = [
    "940GZZLUBXN",   # Brixton
    "940GZZLUSKW",   # Stockwell
    "940GZZLUVXL",   # Vauxhall
    "940GZZLUPCO",   # Pimlico    
    "940GZZLUVIC",   # Victoria
    "940GZZLUGPK",   # Green Park
    "940GZZLUOXC",   # Oxford Circus
    "940GZZLUWRR",   # Warren Street
    "940GZZLUEUS",   # Euston
    "940GZZLUKSX",   # King's Cross St. Pancras
    "940GZZLUHAI",   # Highbury & Islington
    "940GZZLUFPK",   # Finsbury Park
    "940GZZLUSVS",   # Seven Sisters
    "940GZZLUTMH",   # Tottenham Hale    
    "940GZZLUBLR",   # Blackhorse Road
    "940GZZLUWWL",   # Walthamstow Central
]

STATE_RANKING = {
    "BETWEEN": 1,
    "ARRIVING": 2,
    "AT": 3,
    "DEPARTED": 4
}

def majority(positions):
    counts = positions.value_counts()
    if len(counts) == 0:
        return None
    return counts.index[0]

def build_trails(located):
    rows = located[located["state"].notna()].copy()

    rows["pos"] = rows["state"] + " " + rows["location_naptan"]

    trails = rows.groupby(["line_id", "vehicle_id", "request_id"]).agg(
        tfl_time=("tfl_newest_timestamp", "first"),
        position=("pos",majority),
        mult_positions=("pos","nunique"),
        direction=("direction",majority)
    ).reset_index()

    trails["disagreed"] = trails["mult_positions"] > 1 # when in the request eacch station gives a difference response which is a very real thing fr some reason so we gotta solve that

    split_string=trails["position"].str.split(" ")

    trails["state"]=split_string.str[0]
    trails["station"]=split_string.str[1]

    trails = trails.sort_values(["line_id","vehicle_id","tfl_time"])

    trails["direction"] = trails.groupby(["line_id","vehicle_id"])["direction"].ffill()

    scores=[]
    for direction, station, state in zip(trails["direction"],trails["station"],trails["state"]):
        scores.append(progress(direction,station,state))
    trails["progress"] = scores

    return trails

def progress (direction,station,state):#calculate how far along the train is on the line

    if direction == "inbound":#southbount
        order = VICTORIA_NORTH_TO_SOUTH
    elif direction == "outbound":#nprthbound
        order = VICTORIA_SOUTH_TO_NORTH
    else:
        return None

    place = order.index(station)

    progress = 4 * place + STATE_RANKING[state]

    return progress

def drop_backwards(trails):
    train = ["line_id","vehicle_id"]

    changed = trails["direction"] != trails.groupby(train)["direction"].shift()

    gap = trails.groupby(train)["tfl_time"].diff().dt.total_seconds()

    new_trip = changed | (gap > 600)#over 10 minutes

    trails["trip"] = new_trip.cumsum()

    best_before = trails.groupby("trip")["progress"].cummax().groupby(trails["trip"]).shift()

    backwards = trails["progress"] < best_before
    return trails[~backwards], backwards.sum()