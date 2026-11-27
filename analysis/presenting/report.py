import pandas

def results_table(health, longest, update, criteria,c3):
    rows = [
        {
            "criteria":1,
                    "measure": "Share of stops with a bracket of 90s or less",
                    "value": criteria["criteria1"],
                    "pass_mark": "At least 0.80",
                    "passed": criteria["criteria1_pass"]            
        },
        {
            "criteria":2,
                    "measure": "Median bracket (s)",
                    "value": criteria["criteria2"],
                    "pass_mark": "60 seconds or less",
                    "passed": criteria["criteria2_pass"]            
        },
        {
            "criteria":3,
                    "measure": "Median abs diff from stopwatch",
                    "value": c3["criteria3"],
                    "pass_mark": "30 seconds or less on at least 20 timings spread over multiple days",
                    "passed": c3["criteria3_pass"]         
        },
        {
            "criteria":4,
                    "measure": "Median seconds between tfl_newest_timestamp data",
                    "value": update["median_s"],
                    "pass_mark": "Simply a measurement",
                    "passed": None         
        },
        {
            "criteria":5,
                    "measure": "Share of requests that failed",
                    "value": health["share"],
                    "pass_mark": "under 0.01",
                    "passed": health["share"] < 0.01         
        },
        {
            "criteria":5,
                    "measure": "Longest gap between successful requests(s)",
                    "value": longest["longest_gaps"],
                    "pass_mark": "Simply a measurement",
                    "passed": longest["longest_gaps"] <= 600
        },
        ]
    return pandas.DataFrame(rows)