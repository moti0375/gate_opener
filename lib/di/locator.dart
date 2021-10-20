import 'package:gate_opener/channel/channel_adapter.dart';
import 'package:gate_opener/channel/channel_adapter_impl.dart';
import 'package:gate_opener/data/datasources/gate_opener_datasource.dart';
import 'package:gate_opener/data/datasources/gate_opener_platform_datasource.dart';
import 'package:gate_opener/data/repository/GateOpenerRepositoryImpl.dart';
import 'package:gate_opener/data/repository/gate_opener_repository.dart';
import 'package:get_it/get_it.dart';

GetIt locator = GetIt.instance;

Future<void> setupLocator() async {
  await setGatesRepository();
}

Future setGatesRepository() async {
  locator.registerLazySingleton<ChannelAdapter>(() => ChannelAdapterImpl());
  locator.registerLazySingleton<GateOpenerDataSource>(() => GateOpenerPlatformDataSource(locator<ChannelAdapter>()));
  locator.registerLazySingleton<GateOpenerRepository>(() => GateOpenerRepositoryImpl(locator<GateOpenerDataSource>()));
}