import pandas

from pathlib import Path

#constant imports

from processing.trails import VICTORIA_NORTH_TO_SOUTH as VNS
from processing.trails import VICTORIA_SOUTH_TO_NORTH as VSN

#processing imports

from processing.load import load_requests as llr
from processing.load import load_predictions as llp

from processing.health import failure_rate as hfr
from processing.health import longest_gap as hlg
from processing.health import empty_runs as her

from processing.freshness import label_freshness as flf
from processing.freshness import update_interval as fui
from processing.freshness import fresh_predictions as ffp

from processing.locations import list_locations as lll
from processing.locations import add_states as las
from processing.locations import add_station_ids as lasi

from processing.trails import build_trails as tbt
from processing.trails import drop_backwards as tdb

from processing.arrivals import infer_arrivals as aia
from processing.arrivals import terminus_arrivals as ata

from processing.criteria import criteria_1_and_2 as cc1a2

#presenting imports

from presenting.report import results_table as rrt

from presenting.charts import bracket_historgram as cbh
from presenting.charts import bracket_cumulative as cbc

from presenting.charts import missed_at_by_station as cmabs

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
criteria = cc1a2(arrivals,terminus)

expected = pandas.concat([arrivals,terminus])

brackets_histogram_png= (Path(__file__).parent / "output" / "brackets_histogram.png")
brackets_cumulative_png= (Path(__file__).parent / "output" / "brackets_cumulative.png")
missesd_at_by_station_png= (Path(__file__).parent / "output" / "missed_at_by_station.png")

station_names = dict(zip(located["naptan_id"], located["station_name"].str.removesuffix(" Underground Station")))

cbh(expected,brackets_histogram_png)
cbc(expected,brackets_cumulative_png)
cmabs(expected, station_names,missesd_at_by_station_png )



'''
results = rrt(health,longest,update,criteria)
csv = Path(__file__).parent/"output"/"pilot_results.csv"
results.to_csv(csv)

print(critera)


print(len(terminus))


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
