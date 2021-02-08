import sys
import netCDF4 as nc
import numpy as np
import time
import os
import json
import math



def create_json(uwin_filepath, vwin_filepath, savedir):
    uwindataset = nc.Dataset(uwin_filepath, 'r')
    vwindataset = nc.Dataset(vwin_filepath, 'r')
    lat = uwindataset.variables['latitude'][:]
    lon = uwindataset.variables['longitude'][:]
    time_stamp = uwindataset.variables['time'][:][0]
    time_stamp = time_stamp - 8 * 3600
    uwin = uwindataset.variables['UGRD_10maboveground'][:]
    vwin = vwindataset.variables['VGRD_10maboveground'][:]
    time_array = time.localtime(time_stamp)
    other_style_time = time.strftime('%Y%m%d%H%M%S', time_array)
    yyyyMMdd = other_style_time[0:8]
    lon_max_line = 105
    lon_min_line = 102.5
    lat_max_line = 31.5
    lat_min_line = 30
    # 对经纬度数组进行截取
    lon_idx = np.where((lon >= float(lon_min_line)) & (lon <= float(lon_max_line)))
    lon_after = lon[lon_idx]
    lat_idx = np.where((lat >= float(lat_min_line)) & (lat <= float(lat_max_line)))
    lat_after = lat[lat_idx]
    lon_min_ind = lon_idx[0][0]
    lon_max_ind = lon_idx[0][-1]
    lat_min_ind = lat_idx[0][0]
    lat_max_ind = lat_idx[0][-1]
    lon_min = float('{:.3f}'.format(lon_after[0]))
    lon_max = float('{:.3f}'.format(lon_after[-1]))
    lat_min = float('{:.3f}'.format(lat_after[0]))
    lat_max = float('{:.3f}'.format(lat_after[-1]))
    lon_count = len(lon_after)
    lat_count = len(lat_after)
    modify_type = 0
    filename = '{}{}'.format(other_style_time, '_windfield.json')
    product = 'windfield'
    uwin_after = uwin[0][lat_min_ind:lat_max_ind + 1, lon_min_ind: lon_max_ind + 1]
    vwin_after = vwin[0][lat_min_ind:lat_max_ind + 1, lon_min_ind: lon_max_ind + 1]
    # 计算风速
    winspeed = np.sqrt(np.square(uwin_after) + np.square(vwin_after))
    # 计算风向
    winddirect = 180 + np.arctan(uwin_after / vwin_after) * 180 / math.pi
    # 取部分的经纬度和风速风向
    part_lon_idx = []
    part_lat_idx = []
    for index, i in enumerate(list(lon_idx[0])):
        if index % 10 == 5:
            part_lon_idx.append(index)
    for index, i in enumerate(list(lat_idx[0])):
        if index % 10 == 5:
            part_lat_idx.append(index)
    part_lon_idx = tuple(part_lon_idx)
    part_lat_idx = tuple(part_lat_idx)
    part_lon_min_idx = part_lon_idx[0]
    part_lon_max_idx = part_lon_idx[-1]
    part_lat_min_idx = part_lat_idx[0]
    part_lat_max_idx = part_lat_idx[-1]
    part_windspeed = winspeed[part_lat_min_idx: part_lat_max_idx + 1: 10, part_lon_min_idx: part_lon_max_idx + 1: 10]
    part_winddirect = winddirect[part_lat_min_idx:part_lat_max_idx + 1: 10, part_lon_min_idx: part_lon_max_idx + 1: 10]
    wind_dic = {}
    wind_dic['modifyType'] = modify_type
    wind_dic['lonMin'] = lon_min
    wind_dic['lonMax'] = lon_max
    wind_dic['latMin'] = lat_min
    wind_dic['latMax'] = lat_max
    wind_dic['lonCount'] = lon_count
    wind_dic['latCount'] = lat_count
    windspeed_list = []
    winddirect_list = []
    part_windspeed_list = []
    part_winddirect_list = []
    for index, j in enumerate(winspeed.flatten().tolist()):
        if j == None:
            j = 999999
        windspeed_list.append(float('{:.1f}'.format(j)))
    for index, j in enumerate(winddirect.flatten().tolist()):
        if j == None:
            j = 999999
        winddirect_list.append(float('{:.1f}'.format(j)))
    for index, j in enumerate(part_windspeed.flatten().tolist()):
        if j == None:
            j = 999999
        part_windspeed_list.append(float('{:.1f}'.format(j)))
    for index, j in enumerate(part_winddirect.flatten().tolist()):
        if j == None:
            j = 999999
        part_winddirect_list.append(float('{:.1f}'.format(j)))
    wind_dic['windSpeed'] = windspeed_list
    wind_dic['windDirection'] = winddirect_list
    wind_dic['partWindSpeed'] = part_windspeed_list
    wind_dic['partWindDirection'] = part_winddirect_list
    wind_dic['filename'] = filename
    wind_dic['time'] = other_style_time
    wind_dic['filename'] = filename
    wind_dic['productType'] = product
    wind_json = json.dumps(wind_dic)
    save_path = '{}{}{}{}'.format(savedir, yyyyMMdd, '/', product.upper())
    if os.path.exists(save_path) == False:
        os.makedirs(save_path)
    wind_path = '{}{}{}'.format(save_path, '/', filename)
    with open(wind_path, 'w+') as file_obj:
        file_obj.write(wind_json)

if __name__ == '__main__':
    uwin_filepath = sys.argv[1]
    vwin_filepath = sys.argv[2]
    savedir = sys.argv[3]
    # uwin_filepath = "D:/Data/Z_NAFP_C_BABJ_20210113051112_P_HRCLDAS_RT_CHN-BCCD_0P01_HOR-UWIN-2021011313.nc"
    # vwin_filepath = "D:/Data/Z_NAFP_C_BABJ_20210113051112_P_HRCLDAS_RT_CHN-BCCD_0P01_HOR-VWIN-2021011313.nc"
    # savedir = "D:/Data/cldas1km/"
    # uwinfilepath = r'D:\\Data\\20210104_000000.grapes.12h.nc'
    # create_json(uwinfilepath, 'D:\\Data\\fusion12h_parse')
    create_json(uwin_filepath, vwin_filepath, savedir)