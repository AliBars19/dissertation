import matplotlib
matplotlib.use("Agg")

import matplotlib.pyplot as pyp
import matplotlib.ticker as pf

BLUE = "#0084ff"
MUTED = "#666666"
GRID = "#E6E6E6"

def bracket_historgram(expected, out_path):# this first function is for the clusters of data
    seconds = expected["bracket_s"].round()

    figure,axis = pyp.subplots(figsize=(8,4.5))

    axis.hist(seconds, bins=range(0,131), color=BLUE)

    axis.axvline(60, color=MUTED, linestyle="--", linewidth=1)
    axis.axvline(90, color=MUTED, linestyle="--", linewidth=1)

    axis.set_title("Arrival brackets at clusters of 30s 60s and 90s", loc="left")
    axis.set_xlabel("Bracket width in seconds")
    axis.set_ylabel("Arrivals")

    axis.spines[["top","right"]].set_visible(False)
    axis.grid(axis="y", color=GRID)
    axis.set_axisbelow(True)

    figure.tight_layout()
    figure.savefig(out_path, dpi=200)
    pyp.close(figure)

def bracket_cumulative(expected, out_path):
    seconds=expected["bracket_s"].round().sort_values()
    n = len(seconds)
    share=[k/n for k in range(1,n+1)]

    figure,axis = pyp.subplots(figsize=(8,4.5))

    axis.step(seconds, share, where="post", color=BLUE, linewidth=1)

    axis.axvline(60, color=MUTED, linestyle="--", linewidth=1)
    axis.axvline(90, color=MUTED, linestyle="--", linewidth=1)
    axis.axhline(0.8, color=MUTED, linestyle="--", linewidth=1)

    at_90 = (seconds <= 90).mean()
    axis.plot(90, at_90, marker="o", color=BLUE)
    axis.annotate(f"{at_90:.0%} within 90s", xy=(90,at_90), xytext=(100, 0.85))

    axis.set_xlim(0,200)
    axis.set_ylim(0,1)
    axis.yaxis.set_major_formatter(pf.PercentFormatter(1))

    axis.set_title("95% of arrivals are known to land within 90 seconds from the API", loc="left")
    axis.set_xlabel("Bracket width in seconds")
    axis.set_ylabel("Share of arrivals")

    axis.spines[["top","right"]].set_visible(False)
    axis.grid(axis="y", color=GRID)
    axis.set_axisbelow(True)

    figure.tight_layout()
    figure.savefig(out_path, dpi=200)
    pyp.close(figure)