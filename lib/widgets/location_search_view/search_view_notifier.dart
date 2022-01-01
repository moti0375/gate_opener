import 'package:flutter/material.dart';
import 'package:gate_opener/data/model/location_search/place_search.dart';
import 'package:gate_opener/data/repository/location_search_repository.dart';
import 'package:geolocator/geolocator.dart';

class SearchViewNotifier with ChangeNotifier {
  Position? currentPosition;
  List<PlaceSearch> searchResults = [];

  LocationSearchRepository _locationSearchRepository;

  SearchViewNotifier(this._locationSearchRepository);

  searchPlace(String searchText) async {
    searchResults = await _locationSearchRepository.searchLocation(searchText);
    debugPrint("searchPlace: result: $searchResults");
    notifyListeners();
  }

  onSearchItemSelected(){
    searchResults.clear();
    notifyListeners();
  }

}