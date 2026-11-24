def list_locations(fresh):
    counts = fresh["current_location"].value_counts()
    return counts