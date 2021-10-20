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