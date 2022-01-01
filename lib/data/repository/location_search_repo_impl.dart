import 'package:gate_opener/data/datasources/location_search/location_search_datasource.dart';
import 'package:gate_opener/data/model/location_search/place_search.dart';
import 'package:gate_opener/data/repository/location_search_repository.dart';

class LocationSearchRepoImpl extends LocationSearchRepository {
  final LocationSearchDataSource _dataSource;

  LocationSearchRepoImpl(this._dataSource);

  @override
  Future<List<PlaceSearch>> searchLocation(String searchText) async {
    return _dataSource.executeLocationSearchQuery(searchText);
  }
}