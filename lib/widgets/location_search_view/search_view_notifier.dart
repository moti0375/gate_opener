import 'dart:async';

import 'package:flutter/material.dart';
import 'package:gate_opener/data/model/location_search/place.dart';
import 'package:gate_opener/data/model/location_search/place_search.dart';
import 'package:gate_opener/data/repository/location_search_repository.dart';
import 'package:geolocator/geolocator.dart';

class SearchViewNotifier with ChangeNotifier {
  Position? currentPosition;
  List<PlaceSearch> searchResults = [];

  LocationSearchRepository _locationSearchRepository;
  StreamController<Place> streamController = StreamController<Place>();

  SearchViewNotifier(this._locationSearchRepository);

  searchPlace(String searchText) async {
    searchResults = await _locationSearchRepository.searchLocation(searchText);
    debugPrint("searchPlace: result: $searchResults");
    notifyListeners();
  }

  _getPlaceById(String placeId) async {
    var place = await _locationSearchRepository.findPlaceById(placeId);
    debugPrint("getPlaceById: place: $place");
    streamController.sink.add(place);
    notifyListeners();
  }

  onSearchItemSelected(String placeId){
    _getPlaceById(placeId);
    searchResults.clear();
    notifyListeners();
  }

  @override
  void dispose() {
    streamController.close();
    super.dispose();
  }

}