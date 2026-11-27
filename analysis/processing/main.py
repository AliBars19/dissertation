from pathlib import Path


from analysis.processing.load import load_requests as llr
from analysis.processing.load import load_predictions as llp

from analysis.processing.health import failure_rate as hfr
from analysis.processing.health import longest_gap as hlg
from analysis.processing.health import empty_runs as her

from analysis.processing.freshness import label_freshness as flf
from analysis.processing.freshness import update_interval as fui
from analysis.processing.freshness import fresh_predictions as ffp

from analysis.processing.locations import list_locations as lll
from analysis.processing.locations import add_states as las
from analysis.processing.locations import add_station_ids as lasi

from analysis.processing.trails import build_trails as tbt
from analysis.processing.trails import drop_backwards as tdb

from analysis.processing.arrivals import infer_arrivals as aia
from analysis.processing.arrivals import terminus_arrivals as ata

from analysis.processing.criteria import criteria_1_and_2 as cc1a2

start = "2026-11-17T14:45:00Z"
end = "2026-11-19T00:00:00Z"

db_path = "/home/alibars/Downloads/UNI/Year 3/dissertation/data/vps-2026-11-19.db"
#hardcoded rn but will change when the time comes

requests = llr(db_path, start , end)
predictions = llp(db_path, start, end)
health = hfr(requests)
longest = hlg(requests)
empty = her(requests)
freshness = flf(requests)
update = fui(freshness)
freshpredic = ffp(predictions,freshness)
parsing = las(freshpredic)
located = lasi(parsing)
trails = tbt(located)
clean_trials,dropped = tdb(trails)
arrivals = aia(clean_trials)
terminus = ata(clean_trials)

critera = cc1a2(arrivals,terminus)

print(critera)





print(len(terminus))
'''


print(len(arrivals))
print(arrivals["seen_at"].sum())
print(arrivals["bracket_s"].median())


print (dropped)
print(len(dropped))
print(dropped["trip"].nunique())


locations=lll(freshpredic)
csv=Path(__file__).parent/"output"/"locations.csv"
locations.to_csv(csv)

print(len(requests))
print(requests.head())

print(len(predictions))
print(predictions.head())

print (health)

print(longest)

print(empty)

print(freshness["label"].value_counts())

print(update)

print(freshpredic)
'''
