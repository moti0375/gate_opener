import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gate_opener/data/model/gate.dart';
import 'package:gate_opener/data/repository/gate_opener_repository.dart';
import 'package:gate_opener/pages/home/home_page_event.dart';
import 'package:gate_opener/pages/home/home_page_state.dart';
import 'package:permission_handler/permission_handler.dart';

class HomePageBloc extends Bloc<HomePageEvent, HomePageState> {
  final GateOpenerRepository repository;

  HomePageBloc(this.repository) : super(GatesLoaded([])) {
    print("HomeBloc Created");
    on<GetAllGates>((event, emit) async => handleLoadGatesEvent(event, emit));
    on<GatesLoadedEvent>((event, emit) => emit(GatesLoaded(event.gates)));

    on<DeleteGate>((event, emit) async => handleDeleteGate(event, emit));
    on<AddGate>((event, emit) async => handleAddGate(event, emit));
    on<RequestLocationPermission>((event, emit) async => _requestLocationPermissions(emit));
    on<RequestActivityPermission>((event, emit) async => _requestActivityPermission(emit));
    on<RequestPhonePermission>((event, emit) async => _requestPhonePermission(emit));
    on<AllPermissionsGranted>((event, emit) async => emit(PermissionGranted()));
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

  void handleAddGate(AddGate event, Emitter<HomePageState> emit) async {
    print("handleAddGate: ");

    if (!await Permission.locationAlways.isGranted) {
      emit(NoLocationPermissionState());
    } else if (!await Permission.activityRecognition.isGranted) {
      emit(NoActivityRecognitionPermission());
    } else if (!await Permission.phone.isGranted) {
      emit(NoPhonePermission());
    } else {
      emit(PermissionGranted());
    }

    // var activityRecognition = await Permission.activityRecognition.request().isGranted;
    // print("activityRecognition: $activityRecognition");
    // var phone = await Permission.phone.request().isGranted;
    // print("phone: $phone");
    // if(activityRecognition && phone){
    // }
  }

  void _requestLocationPermissions(Emitter emit) async {
    print("_requestLocationPermissions: ");
    bool isShown = await Permission.location.shouldShowRequestRationale;
    print("location granted: isShown: $isShown");
    var locationGranted = await Permission.location.request().isGranted;
    if (locationGranted) {
      print("location granted: ");
      var alwaysGranted = await Permission.locationAlways.request().isGranted;
      if(alwaysGranted){
        add(AddGate());
      }
    }
  }

  void _requestActivityPermission(Emitter emitter) async {
    if(await Permission.activityRecognition.request().isGranted){
      add(AddGate());
    }
  }

  void _requestPhonePermission(Emitter emitter) async {
    if(await Permission.phone.request().isGranted){
      add(AddGate());
    }
  }

// Map<Permission, PermissionStatus> statuses = await [
//   Permission.locationWhenInUse,
//   Permission.locationAlways,
//   Permission.activityRecognition,
//   Permission.phone
// ].request();

// bool allGranted = !statuses.values.any((element) => !element.isGranted);
// print("handleAddGate: AllGranted: $allGranted");
}
