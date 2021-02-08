import netCDF4 as nc
import numpy as np
import matplotlib.pyplot as plt
import json
import os
import sys

def resolve_product(filepath, savedir):
    # 获得文件名
    filename = filepath.split('\\\\')[-1]
    # 获得产品类型
    product_type = filename.split('-')[-2]
    # 获得时间
    yyyyMMddHH = filename.split('-')[-1].replace('.nc', '')
    yyyyMMddHHmmss = '{}{}'.format(yyyyMMddHH, '0000')
    yyyyMMdd = yyyyMMddHH[0:8]
    type = ''
    param_product = ''
    if product_type == 'TAIR':
        type = 'tem'
        param_product = 'TMP_2maboveground'
    elif product_type == 'PRE':
        type = 'rain1h'
        param_product = 'APCP_surface'
    product_filename = '{}{}{}{}'.format(yyyyMMddHHmmss, '_', type, '.json')
    dataset = nc.Dataset(filepath, 'r')
    tem_orignal = dataset.variables[param_product][:][0]
    lat_orignal = dataset.variables['latitude'][:]
    lon_orignal = dataset.variables['longitude'][:]
    if type == 'tem':
        tem_after = np.array(tem_orignal) - 273.15
    else:
        tem_after = np.array(tem_orignal)
    lon_max_line = 105
    lon_min_line = 102.5
    lat_max_line = 31.5
    lat_min_line = 30

    lon_orignal = np.array(lon_orignal)
    lat_orignal = np.array(lat_orignal)
    lon_idx = np.where((lon_orignal >= float(lon_min_line)) & (lon_orignal <= float(lon_max_line)))
    lon_after = lon_orignal[lon_idx]
    lat_idx = np.where((lat_orignal >= float(lat_min_line)) & (lat_orignal <= float(lat_max_line)))
    lat_after = lat_orignal[lat_idx]
    # 最大最小经纬度
    lon_min = lon_after[0]
    lon_max = lon_after[-1]
    lat_min = lat_after[0]
    lat_max = lat_after[-1]
    # 经纬度数量
    lon_count = len(lon_after)
    lat_count = len(lat_after)

    lat_min_ind = lat_idx[0][0]
    lat_max_ind = lat_idx[0][-1]
    lon_min_ind = lon_idx[0][0]
    lon_max_ind = lon_idx[0][-1]
    tem = tem_after[lat_min_ind:lat_max_ind + 1, lon_min_ind: lon_max_ind + 1]
    # 按照间隔进行取值
    part_lon_ind = []
    part_lat_ind = []
    for index, i in enumerate(lon_idx[0]):
        if index % 10 == 5:
            part_lon_ind.append(i)
    for index, i in enumerate(lat_idx[0]):
        if index % 10 == 5:
            part_lat_ind.append(i)
    part_lon_after = lon_orignal[part_lon_ind]
    part_lat_after = lat_orignal[part_lat_ind]
    part_tem = []
    for i in part_lat_ind:
        for j in part_lon_ind:
            part_tem.append(float('{:.1f}'.format(tem_after[i][j])))

    part_lonlat = []
    for i in part_lat_after:
        for j in part_lon_after:
            tem_lonlat = '{}{}{}'.format(float('{:.3f}'.format(j)), ' ', float('{:.3f}'.format(i)))
            part_lonlat.append(tem_lonlat)

    json_dic = {}
    json_dic['modifyType'] = 0
    json_dic['lonCount'] = lon_count
    json_dic['latCount'] = lat_count
    json_dic['lonMax'] = float('{:.3f}'.format(lon_max))
    json_dic['lonMin'] = float('{:.3f}'.format(lon_min))
    json_dic['latMax'] = float('{:.3f}'.format(lat_max))
    json_dic['latMin'] = float('{:.3f}'.format(lat_min))
    json_dic['filename'] = product_filename
    json_dic['time'] = yyyyMMddHHmmss
    json_dic['productType'] = type
    json_dic['data'] = [float('{:.1f}'.format(j)) for j in tem.flatten().tolist()]
    json_dic['partLonlat'] = part_lonlat
    json_dic['partValue'] = part_tem

    savedir = '{}{}{}{}'.format(savedir, yyyyMMdd, '\\', type.upper())
    if not os.path.exists(savedir):
        os.makedirs(savedir)
    savepath = '{}{}{}'.format(savedir, '\\', product_filename)
    json_content = json.dumps(json_dic)
    with open(savepath, 'w+') as file_obj:
        file_obj.write(json_content)

    # part_tem = np.array(part_tem).reshape(15, 25)
    # m, n = np.meshgrid(part_lon_after, part_lat_after)
    # plt.contourf(m, n, part_tem, 8, cmap='plasma')
    # plt.show()


if __name__ == '__main__':
    # filepath = 'D:\\Data\\Z_NAFP_C_BABJ_20210129141035_P_HRCLDAS_RT_CHN-BCCD_0P01_HOR-TAIR-2021012914.nc'
    # savedir = 'D:\\Data\\cldas_1km_parse\\'
    filepath = sys.argv[1]
    savedir = sys.argv[2]
    resolve_product(filepath, savedir)
