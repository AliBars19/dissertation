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