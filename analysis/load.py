import sqlite3
import pandas

def load_requests(db_path, start, end):
    con = sqlite3.connect(f"file:{db_path}?mode=ro", uri=True)
    sql = "SELECT * FROM tflrequests WHERE sent_at >= ? AND sent_at < ?;"
    try:
        return pandas.read_sql_query(sql, con, params=(start,end))
    except Exception as e:
        print("couldnt read SQL query")
        raise e
    finally:
        con.close()