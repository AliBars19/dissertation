from pathlib import Path

from load import load_requests as llr
from load import load_predictions as llp

from health import failure_rate as hfr
from health import longest_gap as hlg
from health import empty_runs as her

from freshness import label_freshness as flf
from freshness import update_interval as fui
from freshness import fresh_predictions as ffp

from locations import list_locations as lll
from locations import add_states as las

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


print (parsing)



'''
locations=lll(freshpredic)
csv=Path(__file__).parent/"output"/"locations.csv"
locations.to_csv(csv)
'''


'''
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
