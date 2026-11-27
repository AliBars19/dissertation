import pandas 

DIRECTIONS = {"Southbound": "inbound", "Northbound":"outbound"}
MAX_GAP = 180

def load_stopwatch(csv):
    timings = pandas.read_csv(csv)
    timings["arrival_utc"] = pandas.to_datetime(timings["arrival_utc"], utc=True, format="ISO8601")#used ol iso for the logger too
    return timings

def match_stopwatch(timings,expected,clean_trials,station_names):
    expected = expected.reset_index(drop=True)

    trip_direction = clean_trials.groupby("trip")["direction"].first()
    expected["direction"] = expected["trip"].map(trip_direction)

    station_ids = {}
    for id,name in station_names.items():
        station_ids[name] = id

    rows=[]

    for _, t in timings.iterrows():#deciding logic whether two sets of data matcj or not

        station = station_ids[t["station"]]
        direction = DIRECTIONS[t["direction"]]

        candidates= expected[(expected["station"] == station) & (expected["direction"] == direction)]

        gaps = (candidates["arrival"] - t["arrival_utc"]).dt.total_seconds()

        if len(gaps) == 0 or gaps.abs().min() > MAX_GAP:
            rows.append({
                "stopwatch": t["arrival_utc"],
                "station": t["station"],
                "matched": False
            })
            continue#skips everything below and goes to the next loop

        best = gaps.abs().idxmin()

        rows.append({
            "stopwatch": t["arrival_utc"],
            "station": t["station"],
            "matched": True,
            "difference_s": gaps[best],
            "vehicle_id": candidates.loc[best,"vehicle_id" ],
            "seen_at": candidates.loc[best,"seen_at" ],
        })

    return pandas.DataFrame(rows)

def criteria3(matches):
    matched = matches[matches["matched"]]
    median_abs = matched["difference_s"].abs().median()
    days = matched["stopwatch"].dt.date.nunique()

    return{
        "matched": len(matched),
        "unmatches": len(matches)-len(matched),
        "days": days,
        "criteria3":median_abs,
        "criteria3_pass": (median_abs <=30) and (len(matched) >= 20) and (days >=2)
    }