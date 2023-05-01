import 'dart:async';
import 'dart:collection';

import 'package:easy_localization/easy_localization.dart' hide TextDirection;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:gate_opener/data/model/gate.dart';
import 'package:gate_opener/data/repository/gate_opener_repository.dart';
import 'package:gate_opener/di/locator.dart';
import 'package:gate_opener/generated/locale_keys.g.dart';
import 'package:gate_opener/res/colors.dart';
import 'package:gate_opener/utils/string_utils.dart';
import 'package:gate_opener/widgets/app_text_view.dart';
import 'package:gate_opener/widgets/custom_dialog.dart';
import 'package:gate_opener/widgets/designed_button.dart';
import 'package:gate_opener/widgets/location_search_view/location_search_view.dart';
import 'package:gate_opener/widgets/location_search_view/search_view_notifier.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:gate_opener/pages/create_or_edit_gate_page/create_or_edit_gate_store.dart';
import 'package:provider/provider.dart';
import 'package:mobx/mobx.dart';
import 'package:geolocator/geolocator.dart';

const String _placesApiKey = "AIzaSyDdK8EedWvwWXwoDHTypqw1q5m_jERHa3M";

class CreateOrEditGatePage extends StatefulWidget {

  final CreateOrEditStore store;

  const CreateOrEditGatePage({Key? key, required this.store}) : super(key: key);

  @override
  _CreateOrEditGatePageState createState() => _CreateOrEditGatePageState();

  static Widget create({Gate? initialGate}) {
    print("CreateOrEditGatePage: create: $initialGate");
    return ChangeNotifierProvider.value(
      value: locator<SearchViewNotifier>(),
      child: Provider<CreateOrEditStore>(
        create: (_) => CreateOrEditStore(locator<GateOpenerRepository>(), initialGate),
        child: Consumer<CreateOrEditStore>(
            builder: (context, store, child) =>
                CreateOrEditGatePage(
                  store: store,
                )),
      ),
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
    mobxDispose ??=
        reaction((_) => widget.store.createOrEditAction, (CreateOrEditAction? value) => _handleActionChange(value));
  }

  @override
  void dispose() {
    mobxDispose?.call();
    super.dispose();
  }

  Widget _buildMapSection() {
    return SizedBox(
        height: MediaQuery.of(context).size.height * 1 / 3,
        child: Observer(
        builder: (_)
    {
      final Marker? availableMarker = widget.store.marker;
      final LatLng? currentLocation = widget.store.location;
      return ClipRRect(
        borderRadius: BorderRadius.all(Radius.circular(16)),
        child: Stack(
          alignment: Alignment.topCenter,
          children: [
            GoogleMap(
              padding: EdgeInsets.only(top: 60),
              markers: availableMarker != null ? Set.of({availableMarker}) : HashSet(),
              onTap: _onMapClicked,
              mapType: MapType.normal,
              myLocationButtonEnabled: true,
              myLocationEnabled: true,
              initialCameraPosition: CameraPosition(
                  target: currentLocation != null ? currentLocation : LatLng(0, 0),
                  zoom: currentLocation != null ? 15 : 10),
              onMapCreated: (GoogleMapController controller) {
                _controller.complete(controller);
                _googleMapController = controller;
              },
              onCameraMove: _onCameraMoved,
            ),
            LocationSearchView(onPlaceSelected: (place) {
              print("onPlaceSelected: $place");
              _onMapClicked(LatLng(place.geometry.location.latitude, place.geometry.location.longitude));
            },),
          ],
        ),
      );
    },)
    ,
    );
  }

  Widget _locationPane() {
    return Observer(
      builder: (_) =>
          Row(
            mainAxisSize: MainAxisSize.min,
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              Expanded(
                child: Container(
                    alignment: Alignment.center,
                    child: Text(
                      "${widget.store.location?.latitude ?? StringUtils.EMPTY_STRING}",
                      style: Theme
                          .of(context)
                          .textTheme
                          .headline3,
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
                    style: Theme
                        .of(context)
                        .textTheme
                        .headline3,
                    maxLines: 1,
                    textAlign: TextAlign.center,
                    overflow: TextOverflow.clip,
                  ),
                ),
              )
            ],
          ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Observer(
            builder: (_) =>
                Text(widget.store.initialGate != null
                    ? LocaleKeys.edit_screen_title.tr()
                    : LocaleKeys.create_screen_title.tr())),
        elevation: 0,
      ),
      resizeToAvoidBottomInset: false,
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            _buildMapSection(),
            Expanded(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  SizedBox(
                    height: 16,
                  ),
                  Expanded(child: _createFormSection(context)),
                  Observer(
                    builder: (_) =>
                        DesignedButton(
                          color: AppColors.shareButton,
                          height: 60,
                          text: LocaleKeys.save.tr(),
                          onPressed: widget.store.formValid ? () => widget.store.submit() : null,
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
    _googleMapController.moveCamera(CameraUpdate.newCameraPosition(camPosition));
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
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          Observer(
            builder: (_) =>
                AppTextView(
                    text: widget.store.name ??= "",
                    rightIcon: Icon(
                      Icons.create,
                      size: 50,
                    ),
                    style: Theme
                        .of(context)
                        .textTheme
                        .headline6,
                    onRightIconClicked: () => widget.store.onSetNameClicked()),
          ),
          Observer(
            builder: (_) =>
                AppTextView(
                  text: widget.store.phoneNumber ??= "",
                  rightIcon: Icon(
                    Icons.phone,
                    size: 50,
                  ),
                  style: Theme
                      .of(context)
                      .textTheme
                      .headline6,
                  textDirection: TextDirection.ltr,
                  onRightIconClicked: () => widget.store.onSetPhoneClicked(),
                ),
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
        icon,
        required ValueChanged onSubmitted,
        TextInputType inputType = TextInputType.text,
        TextDirection? textDirection}) async {
    showDialog(
        context: context,
        builder: (context) =>
            CustomDialog(
              inputDialog: true,
              initialValue: initialValue,
              title: title,
              description: description,
              icon: icon,
              onSubmitted: onSubmitted,
              textInputType: inputType,
              inputTextDirection: textDirection,
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
          _moveMapToPosition((action as OnLocationUpdated).position, currentZoom);
          break;
        }
      case ShowSetNameDialog:
        {
          print("ShowSetNameDialog: ${(action as ShowSetNameDialog).name}");
          _showDialog(context,
              initialValue: action.name,
              title: LocaleKeys.name_edit_title.tr(),
              description: LocaleKeys.name_edit_subtitle.tr(),
              icon: Icon(
                Icons.description,
                size: 50,
              ),
              onSubmitted: (text) {
                print("onSubmitted: $text");
                widget.store.setName(text);
              });
          break;
        }
      case ShowEditPhoneDialog:
        {
          _showDialog(context,
              initialValue: (action as ShowEditPhoneDialog).phoneNumber,
              title: LocaleKeys.phone_edit_title.tr(),
              description: LocaleKeys.phone_edit_subtitle.tr(),
              icon: Icon(
                Icons.settings_cell_outlined,
                size: 50,
              ),
              onSubmitted: (text) => widget.store.setPhoneNumber(text),
              inputType: TextInputType.phone,
              textDirection: TextDirection.ltr);
        }
    }
  }

}
