import sys
import netCDF4 as nc
import numpy as np
import time
import os
import json
import math

def create_json(filepath, savedir):
    dataset = nc.Dataset(filepath, 'r')
    filename = filepath.split('/')[-1]
    lat = dataset.variables['latitude'][:]
    lon = dataset.variables['longitude'][:]
    time_shape = dataset.variables['r2m'].shape[0]
    temp = dataset.variables['t2m'][:]
    rhu = dataset.variables['r2m'][:]
    prs = dataset.variables['msl'][:]
    pre = dataset.variables['tp'][:]
    # windu = dataset.variables['u10'][:]
    # windv = dataset.variables['v10'][:]
    # 从文件名截取时间（该时间为国际时）
    time_str = filename[0: 15]
    # 转为时间数组
    time_array = time.strptime(time_str, '%Y%m%d_%H%M%S')
    # 转为时间戳
    time_stamp = int(time.mktime(time_array))
    time_stamp += 8 * 3600
    time_array = time.localtime(time_stamp)
    other_style_time = time.strftime('%Y%m%d%H%M%S', time_array)
    yyyyMM = other_style_time[0: 8]
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
    lon_min = float('{:.2f}'.format(lon_after[0]))
    lon_max = float('{:.2f}'.format(lon_after[-1]))
    lat_min = float('{:.2f}'.format(lat_after[0]))
    lat_max = float('{:.2f}'.format(lat_after[-1]))
    lon_count = len(lon_after)
    lat_count = len(lat_after)
    for i in range(time_shape):
        temp_after = temp[i][lat_min_ind:lat_max_ind + 1, lon_min_ind: lon_max_ind + 1]
        rhu_after = rhu[i][lat_min_ind:lat_max_ind + 1, lon_min_ind: lon_max_ind + 1]
        prs_after = prs[i][lat_min_ind:lat_max_ind + 1, lon_min_ind: lon_max_ind + 1]
        pre_after = pre[i][lat_min_ind:lat_max_ind + 1, lon_min_ind: lon_max_ind + 1]
        # windu_after = windu[i][lat_min_ind:lat_max_ind + 1, lon_min_ind: lon_max_ind + 1]
        # windv_after = windv[i][lat_min_ind:lat_max_ind + 1, lon_min_ind: lon_max_ind + 1]
        # # 计算风速
        # windspeed = np.sqrt(np.square(windu_after) + np.square(windv_after))
        # # 计算风向
        # winddirect = 180 + np.arctan(windu_after / windv_after) * 180 / math.pi
        # 将温度k转换为c
        temp_after = temp_after - 273.15
        # 保留一位小数
        temp_after = np.around(temp_after, 1)
        rhu_after = np.around(rhu_after, 1)
        prs_after = np.around(prs_after, 1)
        pre_after = np.around(pre_after, 1)
        temp_filename = '{}{}{}{}{}{}'.format(other_style_time, '_', i, '_', 0, '_tem.json')
        rhu_filename = '{}{}{}{}{}{}'.format(other_style_time, '_', i, '_', 0, '_rhu.json')
        prs_filename = '{}{}{}{}{}{}'.format(other_style_time, '_', i, '_', 0, '_prs.json')
        pre_filename = '{}{}{}{}{}{}'.format(other_style_time, '_', i, '_', 0, '_rain.json')
        # windfield_filename = '{}{}{}{}{}{}'.format(other_style_time, '_', i, '_', 0, '_windfield.json')
        temp_dic = {}
        rhu_dic = {}
        prs_dic = {}
        pre_dic = {}
        # wind_dic = {}
        temp_dic['modifyType'] = 0
        temp_dic['latCount'] = lat_count
        temp_dic['lonMax'] = lon_max
        temp_dic['data'] = [float('{:.1f}'.format(j)) for j in temp_after.flatten().tolist()]
        temp_dic['lonCount'] = lon_count
        temp_dic['latMin'] = lat_min
        temp_dic['forecastCount'] = int(i)
        temp_dic['layer'] = 0
        temp_dic['filename'] = temp_filename
        temp_dic['time'] = other_style_time
        temp_dic['lonMin'] = lon_min
        temp_dic['latMax'] = lat_max
        temp_dic['productType'] = 'tem'

        rhu_dic['modifyType'] = 0
        rhu_dic['latCount'] = lat_count
        rhu_dic['lonMax'] = lon_max
        rhu_dic['data'] = [float('{:.1f}'.format(j)) for j in rhu_after.flatten().tolist()]
        rhu_dic['lonCount'] = lon_count
        rhu_dic['latMin'] = lat_min
        rhu_dic['forecastCount'] = int(i)
        rhu_dic['layer'] = 0
        rhu_dic['filename'] = rhu_filename
        rhu_dic['time'] = other_style_time
        rhu_dic['lonMin'] = lon_min
        rhu_dic['latMax'] = lat_max
        rhu_dic['productType'] = 'rhu'

        prs_dic['modifyType'] = 0
        prs_dic['latCount'] = lat_count
        prs_dic['lonMax'] = lon_max
        prs_dic['data'] = [float('{:.1f}'.format(j)) for j in prs_after.flatten().tolist()]
        prs_dic['lonCount'] = lon_count
        prs_dic['latMin'] = lat_min
        prs_dic['forecastCount'] = int(i)
        prs_dic['layer'] = 0
        prs_dic['filename'] = prs_filename
        prs_dic['time'] = other_style_time
        prs_dic['lonMin'] = lon_min
        prs_dic['latMax'] = lat_max
        prs_dic['productType'] = 'prs'

        pre_dic['modifyType'] = 0
        pre_dic['latCount'] = lat_count
        pre_dic['lonMax'] = lon_max
        pre_dic['data'] = [float('{:.1f}'.format(j)) for j in pre_after.flatten().tolist()]
        pre_dic['lonCount'] = lon_count
        pre_dic['latMin'] = lat_min
        pre_dic['forecastCount'] = int(i)
        pre_dic['layer'] = 0
        pre_dic['filename'] = pre_filename
        pre_dic['time'] = other_style_time
        pre_dic['lonMin'] = lon_min
        pre_dic['latMax'] = lat_max
        pre_dic['productType'] = 'rain'

        # wind_dic['modifyType'] = 0
        # wind_dic['latCount'] = lat_count
        # wind_dic['lonMax'] = lon_max
        # wind_dic['windSpeed'] = [float('{:.1f}'.format(j)) for j in windspeed.flatten().tolist()]
        # wind_dic['windDirection'] = [float('{:.1f}'.format(j)) for j in winddirect.flatten().tolist()]
        # wind_dic['lonCount'] = lon_count
        # wind_dic['latMin'] = lat_min
        # wind_dic['forecastCount'] = int(i)
        # wind_dic['layer'] = 0
        # wind_dic['filename'] = windfield_filename
        # wind_dic['time'] = other_style_time
        # wind_dic['lonMin'] = lon_min
        # wind_dic['latMax'] = lat_max
        # wind_dic['productType'] = 'windfield'

        temp_json = json.dumps(temp_dic)
        rhu_json = json.dumps(rhu_dic)
        prs_json = json.dumps(prs_dic)
        pre_json = json.dumps(pre_dic)
        # wind_json = json.dumps(wind_dic)
        temp_dir = '{}{}{}{}{}'.format(savedir, '\\\\', yyyyMM, '\\\\', 'TEM')
        rhu_dir = '{}{}{}{}{}'.format(savedir, '\\\\', yyyyMM, '\\\\', 'RHU')
        prs_dir = '{}{}{}{}{}'.format(savedir, '\\\\', yyyyMM, '\\\\', 'PRS')
        pre_dir = '{}{}{}{}{}'.format(savedir, '\\\\', yyyyMM, '\\\\', 'RAIN')
        # wind_dir = '{}{}{}{}{}'.format(savedir, '\\\\', yyyyMM, '\\\\', 'WINDFIELD')
        temp_path = '{}{}{}{}{}{}{}'.format(savedir, '\\\\', yyyyMM, '\\\\', 'TEM', '\\\\', temp_filename)
        rhu_path = '{}{}{}{}{}{}{}'.format(savedir, '\\\\', yyyyMM, '\\\\', 'RHU', '\\\\', rhu_filename)
        prs_path = '{}{}{}{}{}{}{}'.format(savedir, '\\\\', yyyyMM, '\\\\', 'PRS', '\\\\', prs_filename)
        pre_path = '{}{}{}{}{}{}{}'.format(savedir, '\\\\', yyyyMM, '\\\\', 'RAIN', '\\\\', pre_filename)
        # wind_path = '{}{}{}{}{}{}{}'.format(savedir, '\\\\', yyyyMM, '\\\\', 'WINDFIELD', '\\\\', windfield_filename)
        if os.path.exists(temp_dir) == False:
            os.makedirs(temp_dir)
        if os.path.exists(rhu_dir) == False:
            os.makedirs(rhu_dir)
        if os.path.exists(prs_dir) == False:
            os.makedirs(prs_dir)
        if os.path.exists(pre_dir) == False:
            os.makedirs(pre_dir)
        # if os.path.exists(wind_dir) == False:
        #     os.makedirs(wind_dir)
        with open(temp_path, 'w+') as file_obj:
            file_obj.write(temp_json)
        with open(rhu_path, 'w+') as file_obj:
            file_obj.write(rhu_json)
        with open(prs_path, 'w+') as file_obj:
            file_obj.write(prs_json)
        with open(pre_path, 'w+') as file_obj:
            file_obj.write(pre_json)
        # with open(wind_path, 'w+') as file_obj:
        #     file_obj.write(wind_json)



if __name__ == '__main__':
    filepath = sys.argv[1]
    savedir = sys.argv[2]
    # filepath = r'D:\\Data\\20210104_000000.grapes.12h.nc'
    # create_json(filepath, 'D:\\Data\\fusion12h_parse')
    create_json(filepath, savedir)