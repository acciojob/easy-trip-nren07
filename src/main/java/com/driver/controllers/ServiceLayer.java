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

        Flight flight=flightMap.getOrDefault(flightId,null);
        if(flight==null) throw new Exception("flight is null");
        Airport ansAirport=null;
        for(Airport airport:airportMap.values()){
            if(airport.getCity().equals(flight.getFromCity())) ansAirport=airport;
        }
        if(ansAirport==null) throw new Exception("airport is null");
        return ansAirport.getAirportName();
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
            if(flight.getFlightDate().equals(date) && (flight.getFromCity().equals(city) || flight.getToCity().equals(city))){
                cnt++;
            }
        }
        return cnt;
    }

}
