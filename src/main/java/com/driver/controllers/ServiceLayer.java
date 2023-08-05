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

    public void addAirport(Airport airport) throws Exception{
        if(airport==null) throw new Exception("airport is null");
        Map<String,Airport> airportMap=repositoryLayerObj.getAirportMap();
        String airportName=airport.getAirportName();
        airportMap.put(airportName,airport);
        repositoryLayerObj.setAirportMap(airportMap);
    }

    public String getLargestAirportName() throws Exception{
        Map<String,Airport> airportMap=repositoryLayerObj.getAirportMap();
        if(airportMap.size()==0) throw new Exception("airport list is empty");
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
        return ansAirport.getAirportName();
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) throws Exception{
        if(fromCity==null || toCity==null) throw new Exception("form city or to city is null");
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

    public void addFlight(Flight flight) throws Exception {
        if(flight==null) throw new Exception("given flight is null");
        int id=flight.getFlightId();
        Map<Integer,Flight>flightMap=repositoryLayerObj.getFlightMap();
        flightMap.put(id,flight);
        repositoryLayerObj.setFlightMap(flightMap);
    }

    public void addPassenger(Passenger passenger) throws Exception{
        if(passenger==null) throw new Exception("passenger is null");
        Map<Integer,Passenger>passengerMap=repositoryLayerObj.getPassengerMap();
        int id=passenger.getPassengerId();
        passengerMap.put(id,passenger);
        repositoryLayerObj.setPassengerMap(passengerMap);
    }

    public String getAirportNameFromFlightId(Integer flightId) throws Exception{
        Map<Integer,Flight>flightMap=repositoryLayerObj.getFlightMap();
        Map<String,Airport>airportMap=repositoryLayerObj.getAirportMap();
        if(!flightMap.containsKey(flightId)) throw new Exception("flight is null");
        Flight flight=flightMap.get(flightId);
        Airport ansAirport=null;
        for(Airport airport:airportMap.values()){
            if(airport.getCity().equals(flight.getFromCity())) ansAirport=airport;
        }
        if(ansAirport==null) throw new Exception("airport is null");
        return ansAirport.getAirportName();
    }

    public String bookATicket(Integer flightId,Integer passengerId) throws Exception{
        Map<Integer,Flight>flightMap=repositoryLayerObj.getFlightMap();
        Map<Integer,Passenger>passengerMap=repositoryLayerObj.getPassengerMap();
        if(!flightMap.containsKey(flightId) || !passengerMap.containsKey(passengerId))
            return "FAILURE";
        Map<Integer,List<Flight>>flightListMap=repositoryLayerObj.getFlightListMap();
        Map<Integer,List<Passenger>>passengerListMap=repositoryLayerObj.getPassengerListMap();
        Flight flight=flightMap.get(flightId);
        Passenger passenger=passengerMap.get(passengerId);
        if(passengerListMap.containsKey(flightId) && passengerListMap.get(flightId).contains(passenger)){
            return "FAILURE";
        }
        if(passengerListMap.containsKey(flightId)){
            if(flight.getMaxCapacity()==passengerListMap.get(flightId).size()) return "FAILURE";
        }
        List<Flight>oldFlightList=flightListMap.getOrDefault(passengerId,new ArrayList<>());
        oldFlightList.add(flight);
        List<Passenger>oldPassengerList=passengerListMap.getOrDefault(flightId,new ArrayList<>());
        oldPassengerList.add(passenger);
        flightListMap.put(passengerId,oldFlightList);
        passengerListMap.put(flightId,oldPassengerList);
        repositoryLayerObj.setFlightListMap(flightListMap);
        repositoryLayerObj.setPassengerListMap(passengerListMap);
        return "SUCCESS";
    }
    public String cancelATicket(Integer flightId,Integer passengerId) throws Exception{
        Map<Integer,Flight>flightMap=repositoryLayerObj.getFlightMap();
        Map<Integer,Passenger>passengerMap=repositoryLayerObj.getPassengerMap();
        if(flightMap.containsKey(flightId) || passengerMap.containsKey(passengerId))
            return "FAILURE";
        Map<Integer,List<Flight>>flightListMap=repositoryLayerObj.getFlightListMap();
        Map<Integer,List<Passenger>>passengerListMap=repositoryLayerObj.getPassengerListMap();
        Flight flight=flightMap.get(flightId);
        Passenger passenger=passengerMap.get(passengerId);
        if(!passengerListMap.containsKey(flightId) || !passengerListMap.get(flightId).contains(passenger)){
            return "FAILURE";
        }
        if(passengerListMap.containsKey(flightId)){
            if(flight.getMaxCapacity()==passengerListMap.get(flightId).size()) return "FAILURE";
        }
        List<Flight>oldFlightList=flightListMap.getOrDefault(passengerId,new ArrayList<>());
        oldFlightList.remove(flight);
        List<Passenger>oldPassengerList=passengerListMap.getOrDefault(flightId,new ArrayList<>());
        oldPassengerList.remove(passenger);
        flightListMap.put(passengerId,oldFlightList);
        passengerListMap.put(flightId,oldPassengerList);
        repositoryLayerObj.setFlightListMap(flightListMap);
        repositoryLayerObj.setPassengerListMap(passengerListMap);
        return "SUCCESS";
    }
    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId)throws Exception{
        Map<Integer, List<Flight>>flightListMap=repositoryLayerObj.getFlightListMap(); //by passengerId
        if(!flightListMap.containsKey(passengerId)) throw new Exception("passengerId not found");
        return flightListMap.get(passengerId).size();
    }

    public int calculateFlightFare(Integer flightId) throws Exception{
        int size=repositoryLayerObj.getPassengerList(flightId).size();
        return 3000+size*50;
    }

    public int getNumberOfPeopleOn(Date date, String airportName) throws Exception{
        Map<Integer,Flight>flightMap=repositoryLayerObj.getFlightMap();
        Map<String,Airport>airportMap=repositoryLayerObj.getAirportMap();
        if(!airportMap.containsKey(airportMap)) throw new Exception("airport Name is not found");
        City city=airportMap.get(airportName).getCity();
        int cnt=0;
        for(Flight flight : flightMap.values() ){
            if(flight.getFlightDate().equals(date) && flight.getFromCity().equals(city)){
                cnt++;
            }else if(flight.getFlightDate().equals(date) && flight.getToCity().equals(city)){
                cnt++;
            }
        }
        return cnt;
    }

}
