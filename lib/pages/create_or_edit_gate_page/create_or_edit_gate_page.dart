import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:gate_opener/data/model/gate.dart';
import 'package:gate_opener/data/repository/gate_opener_repository.dart';
import 'package:gate_opener/di/locator.dart';
import 'package:gate_opener/res/colors.dart';
import 'package:gate_opener/utils/string_utils.dart';
import 'package:gate_opener/widgets/app_text_view.dart';
import 'package:gate_opener/widgets/custom_dialog.dart';
import 'package:gate_opener/widgets/designed_button.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:gate_opener/pages/create_or_edit_gate_page/create_or_edit_gate_store.dart';
import 'package:provider/provider.dart';
import 'package:mobx/mobx.dart';
import 'package:geolocator/geolocator.dart';

class CreateOrEditGatePage extends StatefulWidget {
  final CreateOrEditStore store;

  const CreateOrEditGatePage({Key? key, required this.store}) : super(key: key);

  @override
  _CreateOrEditGatePageState createState() => _CreateOrEditGatePageState();

  static Widget create({Gate? initialGate}) {
    return Provider<CreateOrEditStore>(
      create: (_) =>
          CreateOrEditStore(locator<GateOpenerRepository>(), initialGate),
      child: Consumer<CreateOrEditStore>(
          builder: (context, store, child) => CreateOrEditGatePage(
                store: store,
              )),
    );
  }
}

class _CreateOrEditGatePageState extends State<CreateOrEditGatePage> {
  bool _serviceEnabled = false;
  final GeolocatorPlatform _geolocatorPlatform = GeolocatorPlatform.instance;

  Completer<GoogleMapController> _controller = Completer();
  late GoogleMapController _googleMapController;
  double currentZoom = 15;
  ReactionDisposer? mobxDispose;

  @override
  void initState() {
    super.initState();
    _initializeMap();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    mobxDispose ??= reaction((_) => widget.store.createOrEditAction,
        (CreateOrEditAction? value) => _handleActionChange(value));
  }

  @override
  void dispose() {
    super.dispose();
    mobxDispose?.call();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Create Gate"),
        elevation: 0,
      ),
      resizeToAvoidBottomInset: false,
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            SizedBox(
              height: MediaQuery.of(context).size.height * 1 / 3,
              child: Observer(
                builder: (_) => ClipRRect(
                  borderRadius: BorderRadius.all(Radius.circular(16)),
                  child: GoogleMap(
                    markers: widget.store.markers,
                    onTap: _onMapClicked,
                    mapType: MapType.normal,
                    myLocationButtonEnabled: true,
                    myLocationEnabled: true,
                    initialCameraPosition: CameraPosition(
                        target: widget.store.location ??= LatLng(0, 0),
                        zoom: 15
                    ),
                    onMapCreated: (GoogleMapController controller) {
                      _controller.complete(controller);
                      _googleMapController = controller;
                    },
                    onCameraMove: _onCameraMoved,
                  ),
                ),
              ),
            ),
            Expanded(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  _createFormSection(context),
                  Observer(
                    builder: (_) => DesignedButton(
                      color: AppColors.shareButton,
                      height: 60,
                      text: "Select",
                      onPressed: widget.store.formValid
                          ? () => widget.store.submit()
                          : null,
                    ),
                  )
                ],
              ),
            )
          ],
        ),
      ),
    );
  }

  void _initializeMap() async {
    Position? locationData = await _getCurrentPosition();
    if (locationData != null) {
      widget.store.initializeMap(LatLng(locationData.latitude, locationData.longitude));
      //widget.store.setLocationChanged(LatLng(locationData.latitude, locationData.longitude));
      // _moveMapToPosition(
      //     LatLng(locationData.latitude, locationData.longitude), currentZoom);
    }
  }

  Future<Position?> _getCurrentPosition() async {
    final hasPermission = await _handlePermission();

    if (!hasPermission) {
      return null;
    }

    final position = await _geolocatorPlatform.getCurrentPosition();
    return position;
  }

  Future<bool> _handlePermission() async {
    LocationPermission permission;

    // Test if location services are enabled.
    _serviceEnabled = await _geolocatorPlatform.isLocationServiceEnabled();
    if (!_serviceEnabled) {
      // Location services are not enabled don't continue
      // accessing the position and request users of the
      // App to enable the location services.

      return false;
    }

    permission = await _geolocatorPlatform.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await _geolocatorPlatform.requestPermission();
      if (permission == LocationPermission.denied) {
        // Permissions are denied, next time you could try
        // requesting permissions again (this is also where
        // Android's shouldShowRequestPermissionRationale
        // returned true. According to Android guidelines
        // your App should show an explanatory UI now.
        return false;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      // Permissions are denied forever, handle appropriately.
      return false;
    }

    // When we reach here, permissions are granted and we can
    // continue accessing the position of the device.
    return true;
  }

  void _onMapClicked(LatLng latLng) {
    widget.store.setLocationChanged(latLng);
  }

  void _moveMapToPosition(LatLng position, zoom) {
    CameraPosition camPosition = CameraPosition(target: position, zoom: zoom);
    _googleMapController
        .moveCamera(CameraUpdate.newCameraPosition(camPosition));
  }

  void _onCameraMoved(CameraPosition position) {
    setState(() {
      currentZoom = position.zoom;
    });
  }

  Widget _createFormSection(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Column(
        mainAxisSize: MainAxisSize.max,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Observer(
            builder: (_) => Row(
              mainAxisSize: MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Expanded(
                  child: Container(
                      alignment: Alignment.center,
                      child: Text(
                        "${widget.store.location?.latitude ?? StringUtils.EMPTY_STRING}",
                        style: Theme.of(context).textTheme.headline3,
                        maxLines: 1,
                        textAlign: TextAlign.center,
                        overflow: TextOverflow.clip,
                      )),
                ),
                SizedBox(
                  height: 35,
                  child: VerticalDivider(
                    thickness: 4,
                  ),
                ),
                Expanded(
                  child: Container(
                    child: Text(
                      "${widget.store.location?.longitude ?? StringUtils.EMPTY_STRING}",
                      style: Theme.of(context).textTheme.headline3,
                      maxLines: 1,
                      textAlign: TextAlign.center,
                      overflow: TextOverflow.clip,
                    ),
                  ),
                )
              ],
            ),
          ),
          SizedBox(
            height: 16,
          ),
          AppTextView(
              text: widget.store.name,
              rightIcon: Icon(
                Icons.create,
                size: 50,
              ),
              style: Theme.of(context).textTheme.headline6,
              onRightIconClicked: () => widget.store.onSetNameClicked()),
          SizedBox(
            height: 16,
          ),
          AppTextView(
            text: widget.store.phoneNumber,
            rightIcon: Icon(
              Icons.phone,
              size: 50,
            ),
            style: Theme.of(context).textTheme.headline6,
            onRightIconClicked: () => widget.store.onSetPhoneClicked(),
          )
        ],
      ),
    );
  }

  _onSaveDone() {
    Navigator.of(context).pop();
  }

  void _showDialog(BuildContext context,
      {required String title,
      required String description,
      String? initialValue,
      Widget? icon,
      required ValueChanged onSubmitted,
      TextInputType inputType = TextInputType.text}) async {
    showDialog(
        context: context,
        builder: (context) => CustomDialog(
              initialValue: initialValue,
              title: title,
              description: description,
              icon: icon,
              onSubmitted: onSubmitted,
              textInputType: inputType,
            ));
  }

  void _handleActionChange(CreateOrEditAction? action) {
    switch (action.runtimeType) {
      case OnGateSaved:
        {
          print("GateSaved: ");
          _onSaveDone();
          break;
        }
      case OnLocationUpdated:
        {
          _moveMapToPosition(
              (action as OnLocationUpdated).position, currentZoom);
          break;
        }
      case ShowSetNameDialog:
        {
          print("ShowSetNameDialog: ${(action as ShowSetNameDialog).name}");
          _showDialog(context,
              initialValue: action.name,
              title: "Edit name",
              description: "Enter gate name",
              icon: Icon(
                Icons.description,
                size: 50,
              ), onSubmitted: (text) {
            print("onSubmitted: $text");
            widget.store.setName(text);
          });
          break;
        }
      case ShowEditPhoneDialog:
        {
          _showDialog(context,
              title: "Edit Phone",
              description: "Enter gate phone number",
              icon: Icon(
                Icons.phone,
                size: 50,
              ),
              onSubmitted: (text) => widget.store.setPhoneNumber(text),
              inputType: TextInputType.phone);
        }
    }
  }
}
