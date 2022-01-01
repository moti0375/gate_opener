import 'package:flutter/material.dart';
import 'package:gate_opener/data/datasources/location_search/location_search_datasource.dart';
import 'package:gate_opener/data/model/location_search/place.dart';
import 'package:gate_opener/data/model/location_search/place_search.dart';
import 'package:http/http.dart' as http;
import 'dart:convert' as convert;

const String _apyKey = "AIzaSyDdK8EedWvwWXwoDHTypqw1q5m_jERHa3M";
const String host = "maps.googleapis.com";
const autoCompletePath = "maps/api/place/autocomplete/json";
const placeDetailsPath = "maps/api/place/details/json";
const String schema = "https";

class LocationSearchDataSourceImpl extends LocationSearchDataSource {

  @override
  Future<List<PlaceSearch>> executeLocationSearchQuery(String queryString) async {
    Map<String, String> params = {'input': queryString, 'key' : _apyKey};
    Uri uri = Uri(scheme: schema, host: host, path: autoCompletePath, queryParameters: params);
    print("executeLocationSearchQuery: uri: $uri");
    var response = await http.get(uri);
    var json = convert.jsonDecode(response.body);
    var jsonResults = json['predictions'] as List;
    var searchList = jsonResults.map((place) => PlaceSearch.fromJson(place)).toList();
    print("executeLocationSearchQuery: searchList: $searchList");
    return searchList;
  }

  @override
  Future<Place> getPlaceById(String placeId) async {
    Map<String, String> params = {'place_id': placeId, 'key' : _apyKey};
    Uri uri = Uri(scheme: schema, host: host, path: placeDetailsPath, queryParameters: params);
    var response = await http.get(uri);
    debugPrint("getPlaceById: ${response.body}");
    var json = convert.jsonDecode(response.body);
    var jsonResults = json['result'] as Map<String, dynamic>;
    return Place.fromJson(jsonResults);

  }
}