import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gate_opener/channel/channel_adapter_impl.dart';
import 'package:gate_opener/data/datasources/gate_opener_platform_datasource.dart';
import 'package:gate_opener/data/repository/GateOpenerRepositoryImpl.dart';
import 'package:gate_opener/pages/create_or_edit_gate_page/create_or_edit_gate_page.dart';
import 'package:gate_opener/pages/home/home_page_bloc.dart';
import 'package:gate_opener/pages/home/home_page_event.dart';
import 'package:gate_opener/pages/home/home_page_state.dart';
import 'package:gate_opener/res/strings.dart';
import 'package:gate_opener/widgets/add_gate_card_item.dart';
import 'package:gate_opener/widgets/app_text_view.dart';
import 'package:gate_opener/widgets/gate_card_item.dart';
import 'package:gate_opener/widgets/gates_list_item.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();

  static Widget create() {
    return BlocProvider(
      create: (_) => HomePageBloc(GateOpenerRepositoryImpl(
          GateOpenerPlatformDataSource(ChannelAdapterImpl()))),
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
          text: MY_GATES_TITLE,
        ),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Center(
            child: BlocBuilder<HomePageBloc, HomePageState>(
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

  Widget _buildGridView(List<ListItemViewModel> gates) {

    List<GateCardItem> items = [GateCardItem(onPressed: navigateToMapPage, onLongPressed: (){},), GateCardItem(onPressed: navigateToMapPage, onLongPressed: (){},)];

    return GridView.count(crossAxisCount: 2,
    children: List.generate(items.length + 1, (index) => index == items.length ? Padding(
      padding: const EdgeInsets.all(4.0),
      child: AddGateCardItem(onPressed: navigateToMapPage),
    ) : Padding(
      padding: const EdgeInsets.all(4.0),
      child: items[index],
    )));
  }

  void navigateToMapPage() {
    Navigator.of(context).push(MaterialPageRoute(builder: (context) => CreateOrEditGatePage()));
  }
}
