package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RepositoryLayer {
    Map<String, Airport> airportMap=new HashMap<>(); // key airport Name
    Map<Integer, Flight>flightMap=new HashMap<>();
    Map<Integer,Passenger>passengerMap=new HashMap<>();
    Map<Integer, List<Passenger>>passengerListMap=new HashMap<>();
    Map<Integer,List<Flight>>flightListMap=new HashMap<>();

    Map<Integer,Integer>bookingMap=new HashMap<>();

    public Map<Integer, Integer> getBookingMap() {
        return bookingMap;
    }

    public void setBookingMap(Map<Integer, Integer> bookingMap) {
        this.bookingMap = bookingMap;
    }

    public Map<Integer, Passenger> getPassengerMap() {
        return passengerMap;
    }

    public void setPassengerMap(Map<Integer, Passenger> passengerMap) {
        this.passengerMap = passengerMap;
    }

    public Map<String, Airport> getAirportMap() {
        return airportMap;
    }

    public void setAirportMap(Map<String, Airport> airportMap) {
        this.airportMap = airportMap;
    }

    public Map<Integer, Flight> getFlightMap() {
        return flightMap;
    }

    public void setFlightMap(Map<Integer, Flight> flightMap) {
        this.flightMap = flightMap;
    }

    public Map<Integer, List<Passenger>> getPassengerListMap() {
        return passengerListMap;
    }

    public void setPassengerListMap(Map<Integer, List<Passenger>> passengerListMap) {
        this.passengerListMap = passengerListMap;
    }

    public Map<Integer, List<Flight>> getFlightListMap() {
        return flightListMap;
    }

    public void setFlightListMap(Map<Integer, List<Flight>> flightListMap) {
        this.flightListMap = flightListMap;
    }

    public List<Flight> getFlights(Integer personId){
        return flightListMap.getOrDefault(personId,new ArrayList<>());
    }

    public List<Passenger> getPassengerList(Integer flightId){
        return passengerListMap.getOrDefault(flightId,new ArrayList<>());
    }
}
