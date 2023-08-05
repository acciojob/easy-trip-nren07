package com.driver.controllers;

import com.driver.model.Flight;
import com.driver.model.Passenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dummy {

    public String bookATicket(Integer flightId,Integer passengerId){
        Map<Integer, Flight> flightMap=repositoryLayerObj.getFlightMap();
        Map<Integer, Passenger>passengerMap=repositoryLayerObj.getPassengerMap();

        if(!flightMap.containsKey(flightId)) return "FAILURE";
        if(!passengerMap.containsKey(passengerId)) return "FAILURE";
        Flight flight=flightMap.getOrDefault(flightId,null);
        Passenger passenger=passengerMap.getOrDefault(passengerId,null);
        if(flight==null || passenger==null || flight.getMaxCapacity()==0) return "FAILURE";
        Map<Integer, List<Flight>>flightListMap=repositoryLayerObj.getFlightListMap();
        Map<Integer,List<Passenger>>passengerListMap=repositoryLayerObj.getPassengerListMap();
        if(passengerListMap.containsKey(flightId) && passengerListMap.get(flightId).contains(passenger)){
            return "FAILURE";
        }
        flight.setMaxCapacity(flight.getMaxCapacity()-1);
        flightMap.put(flightId,flight);
        repositoryLayerObj.setFlightMap(flightMap);
        List<Flight>flightList=flightListMap.getOrDefault(passengerId,new ArrayList<>());
        flightList.add(flight);
        flightListMap.put(passengerId,flightList);
        repositoryLayerObj.setFlightListMap(flightListMap);
        List<Passenger>passengerList=passengerListMap.getOrDefault(flightId,new ArrayList<>());
        passengerList.add(passenger);
        passengerListMap.put(flightId,passengerList);
        repositoryLayerObj.setPassengerListMap(passengerListMap);
        return "SUCCESS";
    }
}
