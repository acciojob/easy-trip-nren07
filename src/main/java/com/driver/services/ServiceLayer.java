package com.driver.services;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import com.driver.repository.RepositoryLayer;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ServiceLayer {
    @Autowired
    private RepositoryLayer repositoryLayerObj;

    public void addAirport(Airport airport)throws NullPointerException{
        Map<String,Airport> airportMap=repositoryLayerObj.getAirportMap();
        if(airport==null) throw new NullPointerException("airport is null");
        String airportName=airport.getAirportName();
        airportMap.put(airportName,airport);
        repositoryLayerObj.setAirportMap(airportMap);
    }

    public String getLargestAirportName() throws NullPointerException{
        Map<String,Airport> airportMap=repositoryLayerObj.getAirportMap();
        String largestAirport="";
        int max=0;
        for(String key :airportMap.keySet()){
            int terminals=airportMap.get(key).getNoOfTerminals();
            if(max<terminals) largestAirport=airportMap.get(key).getAirportName();
            else if(max==terminals){
                if(largestAirport.compareTo(airportMap.get(key).getAirportName())>0){
                    largestAirport=largestAirport;
                }else{
                    largestAirport=airportMap.get(key).getAirportName();
                }
            }
        }
        if(largestAirport==null) throw new NullPointerException("airport is null");
        return largestAirport;
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity){
        if(fromCity==null || toCity==null) throw new NullPointerException("provided city is null");
        Map<Integer, Flight> flightMap=repositoryLayerObj.getFlightMap();
        double min=-1;
        for(Flight flight : flightMap.values()){
            if(flight.getFromCity().equals(fromCity) && flight.getToCity().equals(toCity)){
                if(min>flight.getDuration()){
                    min=flight.getDuration();
                }
            }
        }
        return min;
    }

    public void addFlight(Flight flight) throws  NullPointerException{
        if(flight==null) throw new NullPointerException("flight data is null");
        int id=flight.getFlightId();

    }

    public void addPassenger(Passenger passenger) throws NullPointerException{
        if(passenger==null) throw new NullPointerException("passenger is null");
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

}
