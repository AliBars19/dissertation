import pandas

def criteria_1_and_2(arrivals,turn_arrivals):

    expected = pandas.concat([arrivals,turn_arrivals])

    brackets = expected["bracket_s"].round()

    usable=(brackets <= 90).sum()
    
    criteria1= usable/len(expected)
    criteria2= brackets.median()

    return {
        "expected_stops": len(expected),
        "criteria1": criteria1,
        "criteria1_pass": criteria1 >= 0.8,
        "criteria2":criteria2,
        "criteria2_pass":criteria2 <= 60,
    }