import 'package:gate_opener/data/model/location_search/place.dart';
import 'package:gate_opener/data/model/location_search/place_search.dart';

abstract class LocationSearchRepository {
  Future<List<PlaceSearch>> searchLocation(String searchText);
  Future<Place> findPlaceById(String placeId);
}