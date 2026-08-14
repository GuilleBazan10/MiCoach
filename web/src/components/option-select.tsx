// Select con label, reutilizado por los formularios de profile/workout/nutrition
// para elegir un valor de un enum del backend (mirror de los DropdownButtonFormField
// de mobile).
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';

interface OptionSelectProps {
  label: string;
  value?: string | null;
  onChange: (value: string) => void;
  options: Record<string, string>;
  placeholder?: string;
  id?: string;
}

export function OptionSelect({ label, value, onChange, options, placeholder = 'Sin especificar', id }: OptionSelectProps) {
  return (
    <div className="flex flex-col gap-1.5">
      <Label htmlFor={id}>{label}</Label>
      <Select value={value ?? undefined} onValueChange={onChange}>
        <SelectTrigger id={id} className="w-full">
          <SelectValue placeholder={placeholder} />
        </SelectTrigger>
        <SelectContent>
          {Object.entries(options).map(([key, text]) => (
            <SelectItem key={key} value={key}>
              {text}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>
  );
}
