import 'package:equatable/equatable.dart';
import 'package:gate_opener/data/model/gate.dart';

abstract class HomePageState extends Equatable{}
class GatesLoaded extends HomePageState{
  final List<Gate> gates;
  GatesLoaded(this.gates);

  @override
  List<Object?> get props => [gates];
  @override
  bool? get stringify => true;
}


abstract class HomePermissionState extends HomePageState{
  @override
  List<Object?> get props => [];
  @override
  bool? get stringify => true;
  @override
  bool operator ==(Object other) {
    return false;
  }
}

class NoLocationPermissionState extends HomePermissionState{}
class NoAlwaysLocationPermission extends HomePermissionState{}
class NoActivityRecognitionPermission extends HomePermissionState{}
class NoPhonePermission extends HomePermissionState{}
class PermissionGranted extends HomePermissionState {}