import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gate_opener/data/model/gate.dart';
import 'package:gate_opener/data/repository/gate_opener_repository.dart';
import 'package:gate_opener/di/locator.dart';
import 'package:gate_opener/generated/locale_keys.g.dart';
import 'package:gate_opener/pages/create_or_edit_gate_page/create_or_edit_gate_page.dart';
import 'package:gate_opener/pages/home/home_page_bloc.dart';
import 'package:gate_opener/pages/home/home_page_event.dart';
import 'package:gate_opener/pages/home/home_page_state.dart';
import 'package:gate_opener/res/colors.dart';
import 'package:gate_opener/widgets/add_gate_card_item.dart';
import 'package:gate_opener/widgets/app_text_view.dart';
import 'package:gate_opener/widgets/custom_dialog.dart';
import 'package:gate_opener/widgets/designed_button.dart';
import 'package:gate_opener/widgets/gate_card_item.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();

  static Widget create() {
    return BlocProvider(
      create: (_) => HomePageBloc(locator<GateOpenerRepository>()),
      child: HomePage(),
    );
  }
}

class _HomePageState extends State<HomePage> {
  @override
  void initState() {
    super.initState();
    HomePageBloc bloc = context.read<HomePageBloc>();
    print("initState: $bloc");
    bloc.add(GetAllGates());
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        elevation: 0,
        title: AppTextView(
          text: LocaleKeys.my_gates.tr(),
        ),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Center(
            child: BlocConsumer<HomePageBloc, HomePageState>(
          listener: (_, state) {
            print("BlocListener: $state");
            if (state is NoLocationPermissionState) {
              _showDialog(
                context,
                title: LocaleKeys.location_permission_title.tr(),
                description: LocaleKeys.location_permission_rational.tr(),
                icon: Icon(
                  Icons.location_on_outlined,
                  size: 35,
                ),
                onSubmitted: (_) {
                  print("onSubmitted");
                  context.read<HomePageBloc>().add(RequestLocationPermission());
                },
              );
            }
            if (state is NoActivityRecognitionPermission) {
              _showDialog(
                context,
                title: LocaleKeys.activity_permission_title.tr(),
                description: LocaleKeys.activity_permission_rational.tr(),
                icon: Icon(
                  Icons.nordic_walking,
                  size: 35,
                ),
                onSubmitted: (_) {
                  print("onSubmitted");
                  context.read<HomePageBloc>().add(RequestActivityPermission());
                },
              );
            }

            if (state is NoPhonePermission) {
              _showDialog(
                context,
                title: LocaleKeys.phone_permission_title.tr(),
                description: LocaleKeys.phone_permission_rational.tr(),
                icon: Icon(
                  Icons.phone_forwarded_sharp,
                  size: 35,
                ),
                onSubmitted: (_) {
                  print("onSubmitted");
                  context.read<HomePageBloc>().add(RequestPhonePermission());
                },
              );
            }

            if (state is PermissionGranted) {
              navigateToMapPage(initialGate: null);
            }
          },
          buildWhen: (_, state) => state is! HomePermissionState,
          builder: (context, state) => _buildPageContent(state),
        )),
      ),
    );
  }

  Widget _buildPageContent(HomePageState state) {
    if (state is GatesLoaded) {
      return _buildGridView(state.gates);
    } else {
      return Container();
    }
  }

  Widget _buildGridView(List<Gate> gates) {
    return GridView.count(
        crossAxisCount: 2,
        children: List.generate(
            gates.length + 1,
            (index) => index == gates.length
                ? Padding(
                    padding: const EdgeInsets.all(4.0),
                    child: AddGateCardItem(
                        onPressed: () =>
                            context.read<HomePageBloc>().add(AddGate())),
                  )
                : Padding(
                    padding: const EdgeInsets.all(4.0),
                    child: GateCardItem(
                      title: gates[index].name,
                      subtitle: gates[index].phoneNumber,
                      onPressed: () =>
                          navigateToMapPage(initialGate: gates[index]),
                      onLongPressed: () => _showDialog(
                        context,
                        title: LocaleKeys.gate_delete_title.tr(),
                        description: LocaleKeys.gate_delete_subtitle.tr(),
                        icon: Icon(Icons.delete, size: 35,),onSubmitted: (_) =>  context.read<HomePageBloc>().add(DeleteGate(gates[index].id!))
                      ),
                    ),
                  )));
  }

  void navigateToMapPage({Gate? initialGate}) {
    Navigator.of(context).push(MaterialPageRoute(
        builder: (context) =>
            CreateOrEditGatePage.create(initialGate: initialGate)));
  }

  void _showDialog(BuildContext context,
      {required String title,
      required String description,
      String? initialValue,
      icon,
      required ValueChanged onSubmitted,
      TextInputType inputType = TextInputType.text}) async {
    showDialog(
        context: context,
        builder: (context) => CustomDialog(
              inputDialog: false,
              initialValue: initialValue,
              title: title,
              description: description,
              icon: icon,
              onSubmitted: onSubmitted,
              textInputType: inputType,
            ));
  }
}
