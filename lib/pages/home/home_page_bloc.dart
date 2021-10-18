import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gate_opener/data/model/gate.dart';
import 'package:gate_opener/data/repository/gate_opener_repository.dart';
import 'package:gate_opener/pages/home/home_page_event.dart';
import 'package:gate_opener/pages/home/home_page_state.dart';
import 'package:gate_opener/widgets/gates_list_item.dart';

class HomePageBloc extends Bloc<HomePageEvent, HomePageState> {
  final GateOpenerRepository repository;

  HomePageBloc(this.repository) : super(GatesLoaded([])) {
    print("HomeBloc Created");
    on<GetAllGates>((event, emit) async => handleLoadGatesEvent(event, emit));
    on<GatesLoadedEvent>((event, emit) => emit(GatesLoaded(event.gates
        .map((e) => ListItemViewModel(e.name, null, Icon(Icons.map),
            bottomRowRightText: e.phoneNumber, bottomRowLeftText: "${e.location.latitude},${e.location.longitude}"))
        .toList())));
  }

  void handleLoadGatesEvent(GetAllGates event, Emitter<HomePageState> emit) {
    repository.fetchAllGates().listen((result) {
      print("handleLoadGates Event: ${result.runtimeType}");
      _onGatesLoaded(result);
    });
  }

  void _onGatesLoaded(List<Gate> gates) {
    add(GatesLoadedEvent(gates));
  }
}
