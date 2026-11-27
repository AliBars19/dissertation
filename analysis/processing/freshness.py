
def label_freshness(requests):
    successful_requests = requests[requests["tfl_newest_timestamp"].notna()].sort_values("sent_at").copy()

    prev = successful_requests["tfl_newest_timestamp"].cummax().shift()

    successful_requests["label"] = "new"
    successful_requests.loc[successful_requests["tfl_newest_timestamp"] == prev, "label"] = "repeat"#function checks whether these tfl_newest_timestamps are fresj from server or are cached/old results 
    successful_requests.loc[successful_requests["tfl_newest_timestamp"] < prev, "label"] = "older"

    return successful_requests

def update_interval(freshness):
    fresh_labels = freshness[freshness["label"] == "new"]
    seconds_between = fresh_labels["tfl_newest_timestamp"].diff().dt.total_seconds()
    median = seconds_between.median()
    quartiles = seconds_between.quantile([0.25,0.75])        
    return {"median_s": median, "quartile_25": quartiles[0.25], "quartile_75": quartiles[0.75], "interval": len(seconds_between)-1}

def fresh_predictions(predictions,freshness):
    new_ids = freshness.loc[freshness["label"] == "new","request_id"]#this function keeps the predictions from only new requests
    return predictions[predictions["request_id"].isin(new_ids)]