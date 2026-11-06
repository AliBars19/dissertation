CREATE TABLE IF NOT EXISTS tflresponses (

    prediction_id INTEGER PRIMARY KEY,
    request_id INTEGER NOT NULL REFERENCES tflrequests(request_id),
    vehicle_id TEXT NOT NULL,
    line_id Integer NOT NULL REFERENCES lines(line_id),
    station_id INTEGER NOT NULL REFERENCES stations(station_id),
    platform_id INTEGER NOT NULL REFERENCES platforms(platform_id),
    direction TEXT,
    destination_station_id INTEGER REFERENCES stations(station_id),
    towards_id INTEGER NOT NULL REFERENCES towards(towards_id),
    location_id INTEGER REFERENCES locations(location_id),
    time_to_station INTEGER NOT NULL,
    expected_arrival TEXT NOT NULL 
);

CREATE TABLE IF NOT EXISTS lines (

    line_id INTEGER primary key,
    tfl_line TEXT unique NOT NULL
);

CREATE TABLE IF NOT EXISTS stations (

    station_id INTEGER primary key,
    naptan_id TEXT unique NOT NULL,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS platforms (

    platform_id INTEGER primary key,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS towards (

    towards_id INTEGER primary key,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS locations (

    location_id INTEGER primary key,
    name TEXT UNIQUE NOT NULL
);


CREATE TABLE IF NOT EXISTS tflrequests (

    request_id INTEGER PRIMARY KEY,
    sent_at TEXT NOT NULL,
    received_at TEXT,
    http_status INTEGER,
    bytes INTEGER ,
    record_count INTEGER ,
    tfl_newest_timestamp TEXT ,
    error TEXT,
    raw_file TEXT,
    lines TEXT NOT NULL
);

