package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import com.driver.controllers.RepositoryLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServiceLayer {
    @Autowired
    private RepositoryLayer repositoryLayerObj;

    public void addAirport(Airport airport){
        Map<String,Airport> airportMap=repositoryLayerObj.getAirportMap();
        String airportName=airport.getAirportName();
        airportMap.put(airportName,airport);
        repositoryLayerObj.setAirportMap(airportMap);
    }

    public String getLargestAirportName(){
        Map<String,Airport> airportMap=repositoryLayerObj.getAirportMap();
        Airport ansAirport=null;
        int max=0;
        for(Airport airport : airportMap.values()){
            int terminals=airport.getNoOfTerminals();
            if(max<terminals) {
                max=terminals;
                ansAirport=airport;
            }
            else if(max==terminals){
                if(ansAirport.getAirportName().compareTo(airport.getAirportName())>0){
                    ansAirport=airport;
                }
            }
        }
        if(ansAirport!=null) return ansAirport.getAirportName();
        return "null";
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity){
        Map<Integer, Flight> flightMap=repositoryLayerObj.getFlightMap();
        double min=-1.0;
        for(Flight flight : flightMap.values()){
            if(flight.getFromCity().equals(fromCity) && flight.getToCity().equals(toCity)){
                if(min>flight.getDuration()){
                    min=flight.getDuration();
                }
            }
        }
        return min;
    }

    public void addFlight(Flight flight) {
        int id=flight.getFlightId();
        Map<Integer,Flight>flightMap=repositoryLayerObj.getFlightMap();
        flightMap.put(id,flight);
        repositoryLayerObj.setFlightMap(flightMap);
    }

    public void addPassenger(Passenger passenger){
        Map<Integer,Passenger>passengerMap=repositoryLayerObj.getPassengerMap();
        int id=passenger.getPassengerId();
        passengerMap.put(id,passenger);
        repositoryLayerObj.setPassengerMap(passengerMap);
    }

    public String getAirportNameFromFlightId(Integer flightId){
        Map<Integer,Flight>flightMap=repositoryLayerObj.getFlightMap();
        Map<String,Airport>airportMap=repositoryLayerObj.getAirportMap();

        Flight flight=flightMap.getOrDefault(flightId,null);
        if(flight==null) return null;

        for(Airport airport:airportMap.values()){
            if(airport.getCity().equals(flight.getFromCity())) return airport.getAirportName();
        }
        return null;
    }

    public String bookATicket(Integer flightId,Integer passengerId){
        Map<Integer,Flight>flightMap=repositoryLayerObj.getFlightMap();
        Flight flight=flightMap.getOrDefault(flightId,null);
        Map<Integer,Passenger>passengerMap=repositoryLayerObj.getPassengerMap();
        Passenger passenger=passengerMap.getOrDefault(passengerId,null);
        if(flight==null || passenger==null || flight.getMaxCapacity()==0) return "FAILURE";
        Map<Integer,List<Flight>>flightListMap=repositoryLayerObj.getFlightListMap();
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
    public String cancelATicket(Integer flightId,Integer passengerId){
        Map<Integer,Flight>flightMap=repositoryLayerObj.getFlightMap();
        Flight flight=flightMap.getOrDefault(flightId,null);
        Map<Integer,Passenger>passengerMap=repositoryLayerObj.getPassengerMap();
        Passenger passenger=passengerMap.getOrDefault(passengerId,null);
        if(flight==null || passenger==null ) return "FAILURE";
        Map<Integer,List<Flight>>flightListMap=repositoryLayerObj.getFlightListMap();
        Map<Integer,List<Passenger>>passengerListMap=repositoryLayerObj.getPassengerListMap();
        if(!passengerListMap.containsKey(flightId) || !passengerListMap.get(flightId).contains(passenger)){
            return "FAILURE";
        }
        flight.setMaxCapacity(flight.getMaxCapacity()+1);
        flightMap.put(flightId,flight);
        repositoryLayerObj.setFlightMap(flightMap);
        List<Flight>flightList=flightListMap.getOrDefault(passengerId,new ArrayList<>());
        flightList.remove(flight);
        flightListMap.put(passengerId,flightList);
        repositoryLayerObj.setFlightListMap(flightListMap);
        List<Passenger>passengerList=passengerListMap.getOrDefault(flightId,new ArrayList<>());
        passengerList.remove(passenger);
        passengerListMap.put(flightId,passengerList);
        repositoryLayerObj.setPassengerListMap(passengerListMap);
        return "SUCCESS";
    }
    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId){
        Map<Integer, List<Flight>>flightListMap=repositoryLayerObj.getFlightListMap(); //by passengerId
        return flightListMap.getOrDefault(passengerId,null).size();
    }

    public int calculateFlightFare(Integer flightId){
        int size=repositoryLayerObj.getPassengerList(flightId).size();
        return 3000+size*50;
    }

    public int getNumberOfPeopleOn(Date date, String airportName){
        Map<Integer,Flight>flightMap=repositoryLayerObj.getFlightMap();
        Map<String,Airport>airportMap=repositoryLayerObj.getAirportMap();
        City city=airportMap.get(airportName).getCity();
        int cnt=0;
        for(Flight flight : flightMap.values() ){
            if(flight.getFlightDate().equals(date) && (flight.getFromCity().equals(city) || flight.getToCity().equals(city))){
                cnt++;
            }
        }
        return cnt;
    }

}