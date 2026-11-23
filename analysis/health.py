def failure_rate(requests):#looks at the amount of times requests failed and calcs a percentage too. all of this will be useful when mr matplotlib comes around
    failed = requests["error"].notna().sum()
    total = len(requests)
    share = failed / total 
    return {"failed": failed, "total": total, "share": share}

def longest_gap(requests):#calculates the longest time in seconds between two entries of "sent_t"
    successful_requests = requests[requests["error"].isna()].sort_values("sent_at")
    gaps = successful_requests["sent_at"].diff().dt.total_seconds()
    longest = gaps.max()
    row = gaps.idxmax()
    ended_at = successful_requests.loc[row,"sent_at"]
    return {"longest_gaps": longest, "ended_at": ended_at}

def empty_runs(requests):#i found out that some of the api results especially late at night before some lines closed simply returned '[]' this is a 200 response but its empty so were accounting for that in our report aswells
    successful_requests = requests[requests["error"].isna()].sort_values("sent_at")
    empty = successful_requests["record_count"] == 0
    run = (empty != empty.shift()).cumsum()

    run = successful_requests[empty].groupby(run[empty]).agg(
        first_req_id=("request_id","first"), 
        last_req_id=("request_id","last") , 
        irst_sent_at=("sent_at","first") , 
        last_sent_at=("sent_at","last") ,
        size=("request_id","size"))
    
    return run