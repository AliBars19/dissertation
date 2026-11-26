import pandas
from trails import VICTORIA_NORTH_TO_SOUTH, VICTORIA_SOUTH_TO_NORTH

def infer_arrivals(clean_trials):
    found=[]

    for trip,g in clean_trials.groupby("trip"):
        updates = list(zip(g["tfl_time"], g["progress"], g["direction"],g["vehicle_id"]))

        for(time1, progress1, direction , vehicle),(time2, progress2, _,_) in zip (updates,updates[1:]):#aight so at first when i wrote this i thought wtf but it acc makes sense so the _ means it can be left blank but u NEED it so it matches the other pair yk

            if direction == "inbound":#southbount
                    order = VICTORIA_NORTH_TO_SOUTH
            elif direction == "outbound":#nprthbound
                    order = VICTORIA_SOUTH_TO_NORTH
            else:
                  continue

            for place in range(16): # num of stations on the line
                  at_value = 4*place+3 # our UNIVERSAL formula for calculating how far along the line we are
                  if progress1< at_value <= progress2:
                        found.append({
                              "trip": trip,
                              "vehicle_id": vehicle,
                              "station": order[place],
                              "before": time1,
                              "after": time2,
                              "bracket_s": (time2-time1).total_seconds(),
                              "arrival": time1 + (time2-time1) / 2,
                              "seen_at": progress2 == at_value,
                        })
                
    return pandas.DataFrame(found)