CREATE TABLE IF NOT EXISTS tflresponses (

    prediction_id INTEGER PRIMARY KEY,
    request_id INTEGER NOT NULL,
    vehicle_id TEXT NOT NULL,
    line_id Integer NOT NULL,
    station_id INTEGER NOT NULL,
    platform_id INTEGER NOT NULL,
    direction TEXT,
    destination_station_id INTEGER,
    towards_id INTEGER NOT NULL,
    location_id INTEGER,
    time_to_station INTEGER NOT NULL,
    expected_arrival TEXT NOT NULL 
);

CREATE TABLE IF NOT EXISTS lines (

    line_id INTEGER primary key,
    tfl_line TEXT unique,
);

CREATE TABLE IF NOT EXISTS stations (

    station_id INTEGER primary key,
    naptan_id TEXT unique,
    name TEXT
);

CREATE TABLE IF NOT EXISTS platforms (

    platform_id INTEGER primary key,
    name TEXT UNIQUE
);

CREATE TABLE IF NOT EXISTS towards (

    towards_id INTEGER primary key,
    name TEXT UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (

    location_id INTEGER primary key,
    text TEXT UNIQUE
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

