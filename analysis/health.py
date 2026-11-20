
def failure_rate(requests):
    failed = requests["error"].notna().sum()
    total = len(requests)
    share = failed / total 
    return {"failed": failed, "total": total, "share": share}

def longest_gap(requests):
    successful_requests = requests[requests["error"].isna()].sort_values("sent_at")
    gaps = successful_requests["sent_at"].diff().dt.total_seconds()
    longest = gaps.max()
    row = gaps.idxmax()
    ended_at = successful_requests.loc[row,"sent_at"]
    return {"longest_gaps": longest, "ended_at": ended_at}
