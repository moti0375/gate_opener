import 'package:gate_opener/channel/channel_adapter.dart';
import 'package:gate_opener/channel/channel_adapter_impl.dart';
import 'package:gate_opener/data/datasources/gate_opener_datasource.dart';
import 'package:gate_opener/data/datasources/gate_opener_platform_datasource.dart';
import 'package:gate_opener/data/datasources/location_search/location_search_datasource.dart';
import 'package:gate_opener/data/datasources/location_search/location_search_datasource_impl.dart';
import 'package:gate_opener/data/repository/GateOpenerRepositoryImpl.dart';
import 'package:gate_opener/data/repository/gate_opener_repository.dart';
import 'package:gate_opener/data/repository/location_search_repo_impl.dart';
import 'package:gate_opener/data/repository/location_search_repository.dart';
import 'package:gate_opener/widgets/location_search_view/search_view_notifier.dart';
import 'package:get_it/get_it.dart';

GetIt locator = GetIt.instance;

Future<void> setupLocator() async {
  await setGatesRepository();
}

Future setGatesRepository() async {
  locator.registerLazySingleton<ChannelAdapter>(() => ChannelAdapterImpl());
  locator.registerLazySingleton<GateOpenerDataSource>(() => GateOpenerPlatformDataSource(locator<ChannelAdapter>()));
  locator.registerLazySingleton<GateOpenerRepository>(() => GateOpenerRepositoryImpl(locator<GateOpenerDataSource>()));

  locator.registerLazySingleton<LocationSearchDataSource>(() => LocationSearchDataSourceImpl());
  locator.registerLazySingleton<LocationSearchRepository>(() => LocationSearchRepoImpl(locator<LocationSearchDataSource>()));
  locator.registerFactory<SearchViewNotifier>(() => SearchViewNotifier(locator<LocationSearchRepository>()));
}