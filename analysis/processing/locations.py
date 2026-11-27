def list_locations(fresh):
    counts = fresh["current_location"].value_counts()
    return counts

def parse_location(text):
    if text == "0" or "Siding" in text or text.endswith("Area"):
        return (None,None)

    if text == "At Platform":
        return("AT","PLATFORM")

    if text.startswith("At ") or text.startswith("Departing "):
        station = text.removeprefix("At ").removeprefix("Departing ")     
        station = station.split(" Platform ")[0]
        return ("AT", station)
    
    if text.startswith("Departed ") or text.startswith("Left "):
        station = text.removeprefix("Departed ").removeprefix("Left ")
        return ("DEPARTED", station)

    if text.startswith("Between "):
        station = text.removeprefix("Between ")
        station = station.split(" and ")[-1]
        return("BETWEEN",  station)

    if text.startswith("Approaching "):
        station = text.removeprefix("Approaching ")
        return("ARRIVING", station)

    return ("UNPARSED", text)

def add_states(fresh):
    parsed = fresh["current_location"].map(parse_location)
    fresh["state"] = parsed.str[0]
    fresh["station_text"] = parsed.str[1]

    return fresh 

def clean (name):#cleaning/unifying the text so we dont have different strings for the same piece of text 
    name = name.lower()
    name = name.removesuffix(" underground station")
    name = name.replace("'", "")
    return name

def match_station(line, text, stations):

    on_line = stations[stations["line_id"] == line]

    target = clean(text)

    for naptan, name in zip(on_line["naptan_id"],on_line["station_name"]):
        if clean(name) == target:
            return naptan

    candidates =[]
    for naptan, name in zip(on_line["naptan_id"],on_line["station_name"]):
        if clean(name).startswith(target):
            candidates.append(naptan)

    if len(candidates) == 1:
        return candidates[0]

    return None

def add_station_ids(fresh):

    stations = fresh[["line_id","naptan_id","station_name"]].drop_duplicates()

    pairs = fresh[["line_id","station_text"]].dropna().drop_duplicates()

    lookup = {}
    for line, text in zip (pairs["line_id"],pairs["station_text"]):
        lookup[(line,text)] = match_station(line,text,stations)

    ids = []
    for line,text in zip (fresh["line_id"],fresh["station_text"]):
        ids.append(lookup.get((line,text)))

    fresh["location_naptan"] = ids

    fresh.loc[ fresh["station_text"] == "PLATFORM", "location_naptan"] = fresh["naptan_id"]
        
    return fresh