from load import load_requests as lr
from load import load_predictions as lp

start = "2026-11-17T14:45:00Z"
end = "2026-11-19T00:00:00Z"

db_path = "/home/alibars/Downloads/UNI/Year 3/dissertation/data/vps-2026-11-19.db"
#hardcoded rn but will change when the time comes

requests = lr(db_path, start , end)
predictions = lp(db_path, start, end)

print(len(requests))
print(requests.head())

print(len(predictions))
print(predictions.head())