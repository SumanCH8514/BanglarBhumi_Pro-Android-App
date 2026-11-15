import React, { useState } from 'react';
import { Calculator, RefreshCw, Layers, Copy, Check, ArrowUpDown } from 'lucide-react';

interface LandCalculatorProps {
  lang: 'en' | 'bn';
}

export const LandCalculator: React.FC<LandCalculatorProps> = ({ lang }) => {
  const [val, setVal] = useState<number>(1);
  const [unit, setUnit] = useState<string>('satak');
  const [copiedId, setCopiedId] = useState<string | null>(null);

  const getDecimalValue = (amount: number, currentUnit: string): number => {
    switch (currentUnit) {
      case 'satak':
        return amount;
      case 'bigha':
        return amount * (14400 / 435.6);
      case 'katha':
        return amount * (720 / 435.6);
      case 'chatak':
        return amount * (45 / 435.6);
      case 'sqft':
        return amount / 435.6;
      case 'acre':
        return amount * 100;
      case 'sqm':
        return (amount * 10.7639) / 435.6;
      default:
        return amount;
    }
  };

  const decVal = getDecimalValue(val || 0, unit);

  const units = [
    { id: 'satak', nameEn: 'Satak / Decimal', nameBn: 'শতক / ডেসিমল', factor: 1, symbol: 'শতক' },
    { id: 'katha', nameEn: 'Katha', nameBn: 'কাঠা', factor: 435.6 / 720, symbol: 'কাঠা' },
    { id: 'bigha', nameEn: 'Bigha', nameBn: 'বিঘা', factor: 435.6 / 14400, symbol: 'বিঘা' },
    { id: 'chatak', nameEn: 'Chatak', nameBn: 'ছটাক', factor: 435.6 / 45, symbol: 'ছটাক' },
    { id: 'acre', nameEn: 'Acre', nameBn: 'একর', factor: 0.01, symbol: 'একর' },
    { id: 'sqft', nameEn: 'Square Feet', nameBn: 'বর্গফুট', factor: 435.6, symbol: 'বর্গফুট' },
    { id: 'sqm', nameEn: 'Square Meter', nameBn: 'বর্গমিটার', factor: 435.6 / 10.7639, symbol: 'বর্গমিটার' },
  ];

  const presets = [
    { label: '1 Satak', bn: '১ শতক', unit: 'satak' },
    { label: '1 Katha', bn: '১ কাঠা', unit: 'katha' },
    { label: '1 Bigha', bn: '১ বিঘা', unit: 'bigha' },
    { label: '1 Acre', bn: '১ একর', unit: 'acre' },
  ];

  const handleCopy = (id: string, textToCopy: string) => {
    navigator.clipboard.writeText(textToCopy);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 1800);
  };

  return (
    <div className="space-y-6">
      {/* 1. Converter Input Card */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-6 space-y-5">
        <div className="flex items-center justify-between gap-4">
          <div>
            <h2 className="text-lg font-bold text-slate-900">
              {lang === 'bn' ? 'বাংলার ভূমি পরিমাপ ক্যালকুলেটর' : 'Bengal Land Unit Converter'}
            </h2>
            <p className="text-xs text-slate-500 mt-0.5">
              {lang === 'bn'
                ? 'শতক, কাঠা, বিঘা ও একরের সঠিক রূপান্তর'
                : 'Accurate Bengal standard area conversion'}
            </p>
          </div>

          <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-800 flex items-center justify-center shrink-0">
            <Calculator className="w-5 h-5 text-emerald-700" />
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="space-y-1.5">
            <label className="block text-xs font-medium text-slate-600">
              {lang === 'bn' ? 'জমির পরিমাণ' : 'Area Value'}
            </label>
            <input
              type="number"
              min="0"
              step="any"
              value={val}
              onChange={e => setVal(parseFloat(e.target.value) || 0)}
              className="w-full bg-white border border-slate-300 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-600/20 rounded-xl px-4 py-2.5 text-base font-semibold text-slate-900 shadow-sm transition-all"
            />
          </div>

          <div className="space-y-1.5">
            <label className="block text-xs font-medium text-slate-600">
              {lang === 'bn' ? 'একক' : 'Unit'}
            </label>
            <select
              value={unit}
              onChange={e => setUnit(e.target.value)}
              className="w-full bg-white border border-slate-300 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-600/20 rounded-xl px-4 py-2.5 text-base font-semibold text-slate-900 shadow-sm transition-all"
            >
              {units.map(u => (
                <option key={u.id} value={u.id}>
                  {lang === 'bn' ? u.nameBn : u.nameEn}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Presets Row */}
        <div className="flex items-center gap-2 pt-1 flex-wrap">
          <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">
            {lang === 'bn' ? 'প্রিসেটস:' : 'PRESETS:'}
          </span>
          {presets.map(p => (
            <button
              key={p.unit}
              onClick={() => { setVal(1); setUnit(p.unit); }}
              className={`px-3 py-1 rounded-lg text-xs font-medium transition-colors ${
                unit === p.unit && val === 1
                  ? 'bg-slate-200 text-slate-900 font-semibold'
                  : 'bg-slate-100 hover:bg-slate-200/80 text-slate-700'
              }`}
            >
              {lang === 'bn' ? p.bn : p.label}
            </button>
          ))}
        </div>
      </div>

      {/* 2. Converted Results Card */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-6 space-y-4">
        <div className="flex items-center justify-between pb-1">
          <h3 className="text-base font-bold text-slate-900">
            {lang === 'bn' ? 'রূপান্তরিত ফলাফল' : 'Converted Results'}
          </h3>
          <ArrowUpDown className="w-4 h-4 text-emerald-600" />
        </div>

        <div className="space-y-2.5">
          {units.map(u => {
            const convertedVal = decVal * u.factor;
            const isSelected = u.id === unit;
            const formatted = convertedVal === 0 ? '0' : parseFloat(convertedVal.toFixed(4)).toString();

            return (
              <div
                key={u.id}
                className={`flex items-center justify-between px-3.5 py-2.5 rounded-xl border transition-all ${
                  isSelected
                    ? 'bg-emerald-50/70 border-emerald-300 text-slate-900'
                    : 'bg-slate-50 border-slate-200/80 text-slate-700'
                }`}
              >
                <div className="flex items-center gap-2">
                  {isSelected && (
                    <span className="w-1.5 h-1.5 rounded-full bg-emerald-600 shrink-0" />
                  )}
                  <span className={`text-sm ${isSelected ? 'font-bold text-slate-900' : 'font-medium text-slate-700'}`}>
                    {lang === 'bn' ? u.nameBn : u.nameEn}
                  </span>
                </div>

                <div className="flex items-center gap-2">
                  <span className="font-bold text-slate-900 text-sm md:text-base">
                    {formatted}
                  </span>
                  <button
                    onClick={() => handleCopy(u.id, formatted)}
                    title="Copy Value"
                    className="p-1 rounded hover:bg-black/5 text-slate-400 hover:text-slate-600 transition-colors"
                  >
                    {copiedId === u.id ? (
                      <Check className="w-3.5 h-3.5 text-emerald-600" />
                    ) : (
                      <Copy className="w-3.5 h-3.5" />
                    )}
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* 3. Formula Card */}
      <div className="p-5 bg-emerald-50 rounded-2xl border border-emerald-100 text-xs text-slate-800 space-y-3">
        <h4 className="font-bold text-slate-900 flex items-center gap-2 text-sm">
          <Layers className="w-4 h-4 text-emerald-700" />
          <span>
            {lang === 'bn' ? 'পশ্চিমবঙ্গের প্রচলিত ভূমি পরিমাপ সূত্র' : 'Standard Bengal Land Units Formula'}
          </span>
        </h4>
        <ul className="space-y-1.5 text-slate-700 font-medium">
          <li className="flex items-start gap-2">
            <span className="text-emerald-700 font-bold">•</span>
            <span>{lang === 'bn' ? '১ বিঘা = ২০ কাঠা = ১৪,৪০০ বর্গফুট (প্রায় ৩৩.০৬ ডেসিমল)' : '1 Bigha = 20 Katha = 14,400 Sq. Ft (~33.06 Decimals)'}</span>
          </li>
          <li className="flex items-start gap-2">
            <span className="text-emerald-700 font-bold">•</span>
            <span>{lang === 'bn' ? '১ কাঠা = ১৬ ছটাক = ৭২০ বর্গফুট (১.৬৫ ডেসিমল)' : '1 Katha = 16 Chatak = 720 Sq. Ft (1.65 Decimals)'}</span>
          </li>
          <li className="flex items-start gap-2">
            <span className="text-emerald-700 font-bold">•</span>
            <span>{lang === 'bn' ? '১ ছটাক = ৪৫ বর্গফুট' : '1 Chatak = 45 Sq. Ft'}</span>
          </li>
          <li className="flex items-start gap-2">
            <span className="text-emerald-700 font-bold">•</span>
            <span>{lang === 'bn' ? '১ ডেসিমল / শতক = ৪৩৫.৬ বর্গফুট = ০.০১ একর' : '1 Decimal / Satak = 435.6 Sq. Ft = 0.01 Acre'}</span>
          </li>
          <li className="flex items-start gap-2">
            <span className="text-emerald-700 font-bold">•</span>
            <span>{lang === 'bn' ? '১ একর = ১০০ শতক = ৩.০২৫ বিঘা' : '1 Acre = 100 Satak = 3.025 Bighas'}</span>
          </li>
        </ul>
      </div>
    </div>
  );
};
