import sqlite3
import pandas


#WHOLE POINT OF THIS FILE IS TO LOAD DATA FROM DB. in thi case its going to be our test db but it will eventually be the 2 week pilot data we

def load_requests(db_path, start, end):
    con = sqlite3.connect(f"file:{db_path}?mode=ro", uri=True)
    sql = "SELECT * FROM tflrequests WHERE sent_at >= ? AND sent_at < ?;"
    try:
        result = pandas.read_sql_query(sql, con, params=(start,end))
        result["sent_at"] = pandas.to_datetime(result["sent_at"], utc=True, format="ISO8601").dt.floor("ms")
        result["received_at"] = pandas.to_datetime(result["received_at"], utc=True, format="ISO8601").dt.floor("ms")
        result["tfl_newest_timestamp"] = pandas.to_datetime(result["tfl_newest_timestamp"], utc=True, format="ISO8601").dt.floor("ms")
        
        return result
    except Exception as e:
        print("couldnt read SQL query")
        raise e
    finally:
        con.close()



def load_predictions(db_path, start, end):
    con = sqlite3.connect(f"file:{db_path}?mode=ro", uri=True)
    sql = """
    
    SELECT  p.prediction_id, r.sent_at, 
            l.tfl_line AS line_id, 
            s.naptan_id, s.name AS station_name, 
            pl.name AS platform_name, 
            t.name AS towards,
            dest.naptan_id AS destination_naptan_id, dest.name AS destination_name,
            loc.name AS current_location,
            
            p.request_id, p.vehicle_id,
            p.direction, p.time_to_station,
            p.expected_arrival,

            r.tfl_newest_timestamp

    FROM tflresponses AS p
    
    JOIN tflrequests AS r ON r.request_id = p.request_id
    JOIN lines AS l ON l.line_id = p.line_id
    JOIN stations AS s ON s.station_id = p.station_id
    JOIN platforms AS pl ON pl.platform_id = p.platform_id
    JOIN towards AS t on t.towards_id = p.towards_id 
    
    LEFT JOIN stations AS dest ON dest.station_id = p.destination_station_id
    LEFT JOIN locations as loc ON loc.location_id = p.location_id

    WHERE r.sent_at >= ? AND r.sent_at < ? 
    
        """
    
    try:
        result = pandas.read_sql_query(sql, con, params=(start,end))
        result["sent_at"] = pandas.to_datetime(result["sent_at"], utc=True, format="ISO8601").dt.floor("ms")
        result["tfl_newest_timestamp"] = pandas.to_datetime(result["tfl_newest_timestamp"], utc=True, format="ISO8601").dt.floor("ms")
        result["expected_arrival"] = pandas.to_datetime(result["expected_arrival"], utc=True, format="ISO8601").dt.floor("ms")

        
        return result
    except Exception as e:
        print("couldnt read SQL query")
        raise e
    finally:
        con.close()