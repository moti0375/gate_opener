import 'package:equatable/equatable.dart';
import 'package:gate_opener/widgets/gates_list_item.dart';

abstract class HomePageState extends Equatable{}
class GatesLoaded extends HomePageState{
  final List<ListItemViewModel> gates;
  GatesLoaded(this.gates);

  @override
  List<Object?> get props => [gates];
  @override
  bool? get stringify => true;
}