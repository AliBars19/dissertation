import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as pyp

BLUE = "#0084ff"
MUTED = "#666666"
GRID = "#E6E6E6"

def bracket_historgram(expected, out_path):
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