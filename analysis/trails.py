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

    return trails