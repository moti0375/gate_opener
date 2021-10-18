
import 'package:equatable/equatable.dart';
import 'package:gate_opener/data/model/location.dart';
import 'package:json_annotation/json_annotation.dart';

part 'gate.g.dart';

@JsonSerializable(explicitToJson: true)
class Gate extends Equatable{
  final String? id;
  final Location location;
  final String name;
  final String phoneNumber;

  Gate(this.id, this.location, this.name, this.phoneNumber);

  factory Gate.fromJson(Map<String, dynamic> json) =>
      _$GateFromJson(json);

  Map<String, dynamic> toJson() => _$GateToJson(this);

  @override
  List<Object?> get props => [id, location, name, phoneNumber];
  @override
  bool? get stringify => true;

}