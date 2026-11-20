def failure_rate(requests):
    failed = requests["error"].notna().sum()
    total = len(requests)
    share = failed / total 
    return {"failed": failed, "total": total, "share": share}