import matplotlib
matplotlib.use("Agg")

import matplotlib.pyplot as pyp
import matplotlib.ticker as pf

from processing.trails import VICTORIA_NORTH_TO_SOUTH

BLUE = "#0084ff"
MUTED = "#666666"
GRID = "#E6E6E6"
GREY = "#b0b0b0"

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

    axis.set_title("95% of arrivals are detected as Arrived within 90 seconds \n from the API which is polled every 30s", loc="left")
    axis.set_xlabel("Bracket width in seconds")
    axis.set_ylabel("Share of arrivals")

    axis.spines[["top","right"]].set_visible(False)
    axis.grid(axis="y", color=GRID)
    axis.set_axisbelow(True)

    figure.tight_layout()
    figure.savefig(out_path, dpi=200)
    pyp.close(figure)

def missed_at_by_station(expected, station_names, out_path):
    missed = (~expected["seen_at"]).groupby(expected["station"]).mean()

    missed = missed.reindex(VICTORIA_NORTH_TO_SOUTH)

    labels = []

    for s in missed.index:
        labels.append(station_names[s])
        
    colours = []

    for s in missed.index:
        if s==VICTORIA_NORTH_TO_SOUTH[0] or s == VICTORIA_NORTH_TO_SOUTH[-1]:
            colours.append(GREY)
        else:
            colours.append(BLUE)

    figure,axis = pyp.subplots(figsize=(8,6))#bars are on the left and the line can be read north to south
    bars = axis.barh(labels, missed.values, color=colours)#so walthamstow is at the top
    axis.invert_yaxis()

    axis.bar_label(bars, labels=[f"{v:.0%}" for v in missed.values], padding=3)

    axis.xaxis.set_major_formatter(pf.PercentFormatter(1))
    axis.set_title("About 1 in 4 arrivals at each station happen between two updates, \n so the train is never seen AT the platform",loc="left")
    axis.set_xlabel("Share of arrivals never seen AT the station")

    axis.spines[["top","right"]].set_visible(False)
    axis.grid(axis="x",color=GRID)

    axis.set_axisbelow(True)

    figure.tight_layout()
    figure.savefig(out_path, dpi=200)
    pyp.close(figure)