import sys
import netCDF4 as nc
import numpy as np
import time
import os
import json
import math
import random


def create_3d_windfield(filepath, savedir):
    dataset = nc.Dataset(filepath)
    filename = filepath.split('/')[-1]
    lon = dataset.variables['longitude'][:]
    lat = dataset.variables['latitude'][:]
    height = dataset.variables['height'][:]
    u_win = dataset.variables['u'][:]
    v_win = dataset.variables['v'][:]
    u_win = u_win.flatten().tolist()
    v_win = v_win.flatten().tolist()
    # 将u_win和v_win里面的无效值替换
    for index, i in enumerate(u_win):
        if i == None:
            u_win[index] = 99999
    for index, i in enumerate(v_win):
        if i == None:
            v_win[index] = 99999
    # 将u_win， v_win转换为二维数组
    u_win = np.array(u_win).reshape(height.shape[0], len(lat), len(lon))
    v_win = np.array(v_win).reshape(height.shape[0], len(lat), len(lon))
    # 取部分的经纬度和风速风向
    part_lon_idx = []
    part_lat_idx = []
    for index, i in enumerate(list(lon)):
        if index % 10 == 5:
            part_lon_idx.append(index)
    for index, i in enumerate(list(lat)):
        if index % 10 == 5:
            part_lat_idx.append(index)
    part_lonlat_list = []
    count = 0
    for i in range(len(lat)):
        for j in range(len(lon)):
            part_lonlat_dic = {}
            part_lonlat_dic['id'] = count
            part_lonlat_dic['lon'] = float('{:.3f}'.format(lon[j]))
            part_lonlat_dic['lat'] = float('{:.3f}'.format(lat[i]))
            count += 1
            part_lonlat_list.append(part_lonlat_dic)
    part_lon_idx = tuple(part_lon_idx)
    part_lat_idx = tuple(part_lat_idx)
    part_lon_min_idx = part_lon_idx[0]
    part_lon_max_idx = part_lon_idx[-1]
    part_lat_min_idx = part_lat_idx[0]
    part_lat_max_idx = part_lat_idx[-1]
    timestr = filename[0: 15]
    time_array = time.strptime(timestr, "%Y%m%d_%H%M%S")
    time_stamp = int(time.mktime(time_array)) + 8 * 3600
    time_array = time.localtime(time_stamp)
    other_style_time = time.strftime('%Y%m%d%H%M%S', time_array)
    yyyyMMdd = other_style_time[0:8]
    modifytype = 0
    lon_count = len(lon)
    lat_count = len(lat)
    product_type = 'windfield'
    for index, i in enumerate(height):
        windfield_dic = {}
        # 计算风速
        winspeed = np.sqrt(np.square(u_win[index]) + np.square(v_win[index]))
        # 计算风向
        winddirect = 180 + np.arctan(np.array(u_win[index]) / np.array(v_win[index])) * 180 / math.pi
        winddirect = winddirect.flatten().tolist()
        winspeed = winspeed.flatten().tolist()
        invalid_index = []
        for index, j in enumerate(winddirect):
            if np.isnan(j):
                winddirect[index] = 999999
                invalid_index.append(index)
            elif j == 225:
                winddirect[index] = 999999
                invalid_index.append(index)
        for index, j in enumerate(winspeed):
            if np.isnan(j):
                winspeed[index] = 999999
            elif int(j) == 141419:
                winspeed[index] = 999999
        # 垂直速度数组
        verticalspeed = []
        for k in range(lon_count * lat_count):
            tem_verticalspeed = float('{:.1f}'.format(random.uniform(-3, 3)))
            verticalspeed.append(tem_verticalspeed)
            # 将对应的值改为无效值
            if k in invalid_index:
                verticalspeed[k] = 999999
        # 转换为2d数组
        winspeed = np.array(winspeed).reshape(len(lat), len(lon))
        winddirect = np.array(winddirect).reshape(len(lat), len(lon))
        verticalspeed = np.array(verticalspeed).reshape(len(lat), len(lon))
        # 局部风速风向垂直速度
        part_windspeed = winspeed[part_lat_min_idx: part_lat_max_idx + 1: 10,
                         part_lon_min_idx: part_lon_max_idx + 1: 10]
        part_winddirect = winddirect[part_lat_min_idx:part_lat_max_idx + 1: 10,
                          part_lon_min_idx: part_lon_max_idx + 1: 10]
        part_verticalspeed = verticalspeed[part_lat_min_idx:part_lat_max_idx + 1: 10,
                          part_lon_min_idx: part_lon_max_idx + 1: 10]
        filename = '{}{}{}{}{}{}'.format(other_style_time, '_', int(i), '_', product_type, '.json')
        windfield_dic['modifyType'] = modifytype
        windfield_dic['lonMin'] = float('{:.3f}'.format(lon[part_lon_min_idx]))
        windfield_dic['lonMax'] = float('{:.3f}'.format(lon[part_lon_max_idx]))
        windfield_dic['latMin'] = float('{:.3f}'.format(lat[part_lat_min_idx]))
        windfield_dic['latMax'] = float('{:.3f}'.format(lat[part_lat_max_idx]))
        windfield_dic['lonCount'] = lon_count
        windfield_dic['latCount'] = lat_count
        windfield_dic['windSpeed'] = [float('{:.1f}'.format(j)) for j in winspeed.flatten().tolist()]
        windfield_dic['windDirection'] = [float('{:.1f}'.format(j)) for j in winddirect.flatten().tolist()]
        windfield_dic['verticalSpeed'] = [float('{:.1f}'.format(j)) for j in verticalspeed.flatten().tolist()]
        windfield_dic['partWindSpeed'] = [float('{:.1f}'.format(j)) for j in part_windspeed.flatten().tolist()]
        windfield_dic['partWindDirection'] = [float('{:.1f}'.format(j)) for j in part_winddirect.flatten().tolist()]
        windfield_dic['partVerticalSpeed'] = [float('{:.1f}'.format(j)) for j in part_verticalspeed.flatten().tolist()]
        windfield_dic['filename'] = filename
        windfield_dic['time'] = timestr
        windfield_dic['productType'] = product_type
        wind_json = json.dumps(windfield_dic)
        save_path = '{}{}{}{}'.format(savedir, yyyyMMdd, '/', product_type.upper())
        if os.path.exists(save_path) == False:
            os.makedirs(save_path)
        wind_path = '{}{}{}'.format(save_path, '/', filename)
        with open(wind_path, 'w+') as file_obj:
            file_obj.write(wind_json)


if __name__ == '__main__':
    # filepath = 'D:/Data/20210125_072400.bkg.nc4'
    # savedir = r'D:/Data/3d_windfield_inversion_parse/'
    filepath = sys.argv[1]
    savedir = sys.argv[2]
    create_3d_windfield(filepath, savedir)
