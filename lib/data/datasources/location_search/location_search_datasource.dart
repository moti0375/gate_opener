import 'package:gate_opener/data/model/location_search/place_search.dart';

abstract class LocationSearchDataSource{
  Future<List<PlaceSearch>> executeLocationSearchQuery(String queryString);
}