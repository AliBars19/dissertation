#THIS DECIDES THE ORDER OF TRAINS AND HOW WERE GOING TO HAVE ONE UNIFIED DIRECTION


def majority(positions):
    return positions.value_counts().index[0]

def build_trails(located):
    rows = located[located["state"].notna()].copy()

    rows["pos"] = rows["state"] + " " + rows["location_naptan"]

    trails = rows.groupby(["line_id", "vehicle_id", "request_id"]).agg(
        tfl_time=("tfl_newest_timestamp", "first"),
        position=("pos",majority),
        mult_positions=("pos","nunique")
    ).reset_index()

    trails["disagreed"] = trails["mult_positions"] > 1 # when in the request eacch station gives a difference response which is a very real thing fr some reason so we gotta solve that

    split_string=trails["position"].str.split(" ")

    trails["state"]=split_string.str[0]
    trails["station"]=split_string.str[1]

    return trails.sort_values(["line_id","vehicle_id","tfl_time"])