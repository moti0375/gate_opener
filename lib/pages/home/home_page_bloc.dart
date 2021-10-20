import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gate_opener/data/model/gate.dart';
import 'package:gate_opener/data/repository/gate_opener_repository.dart';
import 'package:gate_opener/pages/home/home_page_event.dart';
import 'package:gate_opener/pages/home/home_page_state.dart';

class HomePageBloc extends Bloc<HomePageEvent, HomePageState> {
  final GateOpenerRepository repository;

  HomePageBloc(this.repository) : super(GatesLoaded([])) {
    print("HomeBloc Created");
    on<GetAllGates>((event, emit) async => handleLoadGatesEvent(event, emit));
    on<GatesLoadedEvent>((event, emit) => emit(GatesLoaded(event.gates)));


    on<DeleteGate>((event, emit) async => handleDeleteGate(event, emit));
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

  void handleDeleteGate(DeleteGate event, Emitter<HomePageState> emit) {
    repository.deleteGate(event.id);
  }

  void handleAddGate(AddGate event, Emitter<HomePageState> emit) {
  }
}
