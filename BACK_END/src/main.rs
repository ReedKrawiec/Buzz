#[macro_use] extern crate rocket;
use notify_rust::Notification;
use std::time::{Duration, SystemTime};
use rocket::State;
use rocket::Request;
use std::{fs, io};
use std::path::Path;
use std::error::Error;
use std::fs::File;
use std::io::BufReader;
use serde::Deserialize;
use std::collections::HashMap;

#[derive(Deserialize, Debug)]
struct NotificationEntryJson{
    icon: String,
    summary: String,
    body: String,
    name: String,
    appName: String,
}

struct NotificationEntry{
    icon: String,
    summary: String,
    body: String,
    appName: String
}

struct Config {
    notificationEntries: HashMap<String,NotificationEntry>
}

#[get("/<notification>")]
fn get(state: &State<Config>, notification:&str) -> String{
    match state.notificationEntries.get(notification){
        Some(entry) => {
            let mut notification = Notification::new();
            notification.summary(&entry.summary);
            notification.appname(&entry.appName);
            if entry.body != "" {
                notification.body(&entry.body);
            }
            if entry.icon != "" {
                notification.icon(&entry.icon);
            }
            notification.show();
            return "Success".to_string();
        },
        None => {
            return "".to_string();
        }
    }
}

#[launch]
fn rocket() -> _ {
    let file = File::open(Path::new("./notifications.json")).unwrap();
    let reader = BufReader::new(file);
    let u:Vec<NotificationEntryJson> = serde_json::from_reader(reader).unwrap();
    let mut config = Config {
        notificationEntries: HashMap::new()
    };
    for entry in u {
        config.notificationEntries.insert(entry.name, NotificationEntry{
            icon: entry.icon,
            body: entry.body,
            summary: entry.summary,
            appName: entry.appName
        });
    }
    rocket::build().mount("/", routes![get]).manage(config)
}

