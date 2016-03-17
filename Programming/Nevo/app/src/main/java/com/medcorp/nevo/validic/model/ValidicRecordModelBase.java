package com.medcorp.nevo.validic.model;

/**
 * Created by gaillysu on 16/3/14.
 */
public class ValidicRecordModelBase {
   String _id;
   String timestamp;
   String utc_offset;
   int steps;
   float distance;
   int floors;
   float elevation;
   int calories_burned;
   String source;
   String source_name;
   String last_updated;

 public String get_id() {
  return _id;
 }

 public void set_id(String _id) {
  this._id = _id;
 }

 public String getTimestamp() {
  return timestamp;
 }

 public void setTimestamp(String timestamp) {
  this.timestamp = timestamp;
 }

 public String getUtc_offset() {
  return utc_offset;
 }

 public void setUtc_offset(String utc_offset) {
  this.utc_offset = utc_offset;
 }

 public int getSteps() {
  return steps;
 }

 public void setSteps(int steps) {
  this.steps = steps;
 }

 public float getDistance() {
  return distance;
 }

 public void setDistance(float distance) {
  this.distance = distance;
 }

 public int getFloors() {
  return floors;
 }

 public void setFloors(int floors) {
  this.floors = floors;
 }

 public float getElevation() {
  return elevation;
 }

 public void setElevation(float elevation) {
  this.elevation = elevation;
 }

 public int getCalories_burned() {
  return calories_burned;
 }

 public void setCalories_burned(int calories_burned) {
  this.calories_burned = calories_burned;
 }

 public String getSource() {
  return source;
 }

 public void setSource(String source) {
  this.source = source;
 }

 public String getSource_name() {
  return source_name;
 }

 public void setSource_name(String source_name) {
  this.source_name = source_name;
 }

 public String getLast_updated() {
  return last_updated;
 }

 public void setLast_updated(String last_updated) {
  this.last_updated = last_updated;
 }
}
