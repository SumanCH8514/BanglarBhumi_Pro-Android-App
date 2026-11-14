import React, { useState } from 'react';
import { Calculator, RefreshCw, Layers } from 'lucide-react';

interface LandCalculatorProps {
  lang: 'en' | 'bn';
}

export const LandCalculator: React.FC<LandCalculatorProps> = ({ lang }) => {
  const [val, setVal] = useState<number>(1);
  const [unit, setUnit] = useState<string>('satak');

  const getDecimalValue = (amount: number, currentUnit: string): number => {
    switch (currentUnit) {
      case 'satak':
        return amount;
      case 'bigha':
        return amount * 33.05785;
      case 'katha':
        return amount * 1.65289;
      case 'chatak':
        return amount * 0.103305;
      case 'sqft':
        return amount / 435.6;
      case 'acre':
        return amount * 100;
      case 'hectare':
        return amount * 247.105;
      default:
        return amount;
    }
  };

  const decVal = getDecimalValue(val || 0, unit);

  const units = [
    { id: 'satak', nameEn: 'Decimal / Satak (শতক / ডেসিমেল)', nameBn: 'শতক / ডেসিমেল (Satak)', factor: 1, symbol: 'শতক' },
    { id: 'katha', nameEn: 'Katha (কাঠা)', nameBn: 'কাঠা (Katha)', factor: 1 / 1.65289, symbol: 'কাঠা' },
    { id: 'bigha', nameEn: 'Bigha (বিঘা)', nameBn: 'বিঘা (Bigha)', factor: 1 / 33.05785, symbol: 'বিঘা' },
    { id: 'chatak', nameEn: 'Chatak (ছটাক)', nameBn: 'ছটাক (Chatak)', factor: 1 / 0.103305, symbol: 'ছটাক' },
    { id: 'sqft', nameEn: 'Square Feet (বর্গফুট)', nameBn: 'বর্গফুট (Sq. Ft)', factor: 435.6, symbol: 'বর্গফুট' },
    { id: 'acre', nameEn: 'Acre (একর)', nameBn: 'একর (Acre)', factor: 0.01, symbol: 'একর' },
    { id: 'hectare', nameEn: 'Hectare (হেক্টর)', nameBn: 'হেক্টর (Hectare)', factor: 1 / 247.105, symbol: 'হেক্টর' },
  ];

  return (
    <div className="bg-white rounded-2xl shadow-xl border border-slate-200 p-6 md:p-8 space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-6 border-b border-slate-100 gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900 flex items-center gap-2">
            <Calculator className="w-5 h-5 text-emerald-600" />
            <span>{lang === 'bn' ? 'পশ্চিমবঙ্গ ভূমি পরিমাপ ও রূপান্তর ক্যালকুলেটর' : 'West Bengal Land Unit Converter'}</span>
          </h2>
          <p className="text-sm text-slate-500 mt-1">
            {lang === 'bn'
              ? 'বিঘা, কাঠা, ছটাক, শতক/ডেসিমেল এবং একর রূপান্তরের সঠিক হিসাব'
              : 'Accurate Bengal land unit conversions for Amin work and property assessment'}
          </p>
        </div>

        <button
          onClick={() => { setVal(1); setUnit('satak'); }}
          className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold text-slate-600 hover:bg-slate-50 transition-colors self-start sm:self-auto"
        >
          <RefreshCw className="w-3.5 h-3.5 text-slate-400" />
          <span>{lang === 'bn' ? 'রিসেট' : 'Reset'}</span>
        </button>
      </div>

      <div className="p-6 bg-emerald-50/50 rounded-2xl border border-emerald-100 flex flex-col md:flex-row gap-6 items-center">
        <div className="w-full md:w-1/2 space-y-2">
          <label className="block text-xs font-bold uppercase tracking-wider text-emerald-950">
            {lang === 'bn' ? 'জমির পরিমাণ প্রবেশ করান' : 'Enter Land Area'}
          </label>
          <input
            type="number"
            min="0"
            step="any"
            value={val}
            onChange={e => setVal(parseFloat(e.target.value) || 0)}
            className="w-full bg-white border border-emerald-300 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-600/20 rounded-xl px-4 py-3.5 text-xl font-bold text-slate-900 shadow-sm"
          />
        </div>

        <div className="w-full md:w-1/2 space-y-2">
          <label className="block text-xs font-bold uppercase tracking-wider text-emerald-950">
            {lang === 'bn' ? 'বর্তমান একক নির্বাচন করুন' : 'Select Current Unit'}
          </label>
          <select
            value={unit}
            onChange={e => setUnit(e.target.value)}
            className="w-full bg-white border border-emerald-300 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-600/20 rounded-xl px-4 py-3.5 text-base font-bold text-slate-900 shadow-sm"
          >
            {units.map(u => (
              <option key={u.id} value={u.id}>
                {lang === 'bn' ? u.nameBn : u.nameEn}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 pt-4">
        {units.map(u => {
          const convertedVal = decVal * u.factor;
          const isSelected = u.id === unit;

          return (
            <div
              key={u.id}
              className={`p-4 rounded-xl border transition-all ${
                isSelected
                  ? 'bg-emerald-800 text-white border-emerald-800 shadow-md scale-[1.02]'
                  : 'bg-white hover:bg-slate-50 border-slate-200 text-slate-800'
              }`}
            >
              <span className={`text-xs block font-medium ${isSelected ? 'text-emerald-200' : 'text-slate-500'}`}>
                {lang === 'bn' ? u.nameBn : u.nameEn}
              </span>
              <div className="mt-2 flex items-baseline justify-between gap-2">
                <span className="text-xl font-black font-mono tracking-tight">
                  {convertedVal.toLocaleString('en-IN', { maximumFractionDigits: 4 })}
                </span>
                <span className={`text-xs font-semibold ${isSelected ? 'text-emerald-100' : 'text-slate-400'}`}>
                  {u.symbol}
                </span>
              </div>
            </div>
          );
        })}
      </div>

      <div className="p-4 bg-slate-50 rounded-xl border border-slate-200 text-xs text-slate-600 space-y-2">
        <h4 className="font-bold text-slate-900 flex items-center gap-1.5">
          <Layers className="w-4 h-4 text-emerald-600" />
          <span>{lang === 'bn' ? 'পশ্চিমবঙ্গ ভূমি পরিমাপের প্রচলিত একক বিবরণী:' : 'Standard Bengal Land Measurement Table:'}</span>
        </h4>
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-2 pt-1 font-mono text-[11px]">
          <div className="bg-white p-2 rounded border border-slate-200/80">১ বিঘা = ২০ কাঠা</div>
          <div className="bg-white p-2 rounded border border-slate-200/80">১ কাঠা = ১৬ ছটাক (৭২০ বর্গফুট)</div>
          <div className="bg-white p-2 rounded border border-slate-200/80">১ শতক = ৪৩৫.৬ বর্গফুট</div>
          <div className="bg-white p-2 rounded border border-slate-200/80">১ একর = ১০০ শতক (৩ বিঘা ৮ কাঠা)</div>
        </div>
      </div>
    </div>
  );
};
